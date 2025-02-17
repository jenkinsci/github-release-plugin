package io.jenkins.plugins.github.release;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SymantecVersion implements Comparable<SymantecVersion>, Serializable {
    public final int major;
    public final int minor;
    public final int revision;

    SymantecVersion(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    @Override
    public int compareTo(SymantecVersion that) {
        return (Integer.compare(this.major, that.major) * 100)
                + (Integer.compare(this.minor, that.minor) * 10)
                + (Integer.compare(this.revision, that.revision));
    }

    private static final Pattern VERSION_PATTERN = Pattern.compile("^[vV]*(\\d+)\\.(\\d+)\\.(\\d+)$");

    public static boolean isSymantecVersion(String version) {
        if (null == version) {
            return false;
        }
        Matcher matcher = VERSION_PATTERN.matcher(version);
        return matcher.matches();
    }

    public static SymantecVersion of(String version) {
        if (null == version) {
            throw new IllegalArgumentException("version cannot be null");
        }
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    String.format("version '%s' does not match pattern '%s'", version, VERSION_PATTERN.pattern()));
        }
        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int revision = Integer.parseInt(matcher.group(3));
        return new SymantecVersion(major, minor, revision);
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", this.major, this.minor, this.revision);
    }

    public SymantecVersion incrementRevision() {
        return new SymantecVersion(this.major, this.minor, this.revision + 1);
    }

    public SymantecVersion incrementMinor() {
        return new SymantecVersion(this.major, this.minor + 1, this.revision);
    }

    public SymantecVersion incrementMajor() {
        return new SymantecVersion(this.major + 1, this.minor, this.revision);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof SymantecVersion)) {
            return false;
        }
        SymantecVersion that = (SymantecVersion) obj;
        return (this.major == that.major) && (this.minor == that.minor) && (this.revision == that.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, revision);
    }
}
