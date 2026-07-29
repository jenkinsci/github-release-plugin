package io.jenkins.plugins.github.release;

import hudson.model.TaskListener;
import io.jenkins.plugins.github.GitHubUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class ListReleaseStepExecution extends SynchronousNonBlockingStepExecution<List<Release>> {
    final ListReleasesStep step;

    protected ListReleaseStepExecution(ListReleasesStep step, StepContext context) {
        super(context);
        this.step = step;
    }

    AbstractReleaseComparator findReleaseComparator(String name) {
        String lower = name.toLowerCase();

        AbstractReleaseComparator result;

        switch (lower) {
            case "symantecversion":
                result = new SymantecVersionReleaseComparator();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Invalid sort by '%s'. Valid options are 'SymantecVersion'", name));
        }
        return result;
    }

    @Override
    protected List<Release> run() throws Exception {
        TaskListener taskListener = this.getContext().get(TaskListener.class);
        GitHub gitHub = GitHubUtils.loginToGithub(this.step, taskListener);
        GHRepository repository = GitHubUtils.getRepository(gitHub, this.step);

        List<GHRelease> allReleases = repository.listReleases().toList();
        List<Predicate<GHRelease>> filters = new ArrayList<>();

        if (null != this.step.tagNamePattern) {
            final Pattern pattern = Pattern.compile(this.step.tagNamePattern);
            filters.add(release -> {
                Matcher matcher = pattern.matcher(release.getTagName());
                return matcher.find();
            });
        }

        if (null != this.step.includeDrafts) {
            filters.add(release -> this.step.includeDrafts == release.isDraft());
        }

        Stream<GHRelease> stream = allReleases.stream();

        for (Predicate<GHRelease> filter : filters) {
            stream = stream.filter(filter);
        }

        Stream<Release> converted = stream.map(Release::from);

        if (null != this.step.sortBy) {
            AbstractReleaseComparator comparator = findReleaseComparator(this.step.sortBy);
            comparator.ascending = this.step.sortAscending;
            converted = converted.sorted(comparator);
        }

        List<Release> result = converted.collect(Collectors.toList());
        return result;
    }
}
