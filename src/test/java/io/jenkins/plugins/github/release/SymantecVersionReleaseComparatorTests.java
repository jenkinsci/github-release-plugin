package io.jenkins.plugins.github.release;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class SymantecVersionReleaseComparatorTests {

    Release release(String input) {
        Release result = new Release(input);
        return result;
    }

    @Test
    public void sort() {
        SymantecVersionReleaseComparator comparator = new SymantecVersionReleaseComparator();

        List<String> expectedASC = List.of("1.0.0", "V1.0.7", "2.0.0", "v2.0.1");
        List<String> expectedDESC = List.of("v2.0.1", "2.0.0", "V1.0.7", "1.0.0");

        List<String> asc = Stream.of("2.0.0", "1.0.0", "v2.0.1", "V1.0.7")
                .map(this::release)
                .sorted(comparator)
                .map(t -> t.tagName)
                .collect(Collectors.toList());

        assertEquals(expectedASC, asc);
        comparator.ascending = false;
        List<String> desc = Stream.of("2.0.0", "1.0.0", "v2.0.1", "V1.0.7")
                .map(this::release)
                .sorted(comparator)
                .map(e -> e.tagName)
                .collect(Collectors.toList());

        assertEquals(expectedDESC, desc);
    }
}
