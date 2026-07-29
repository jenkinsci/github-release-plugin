package io.jenkins.plugins.github.release;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class SymantecVersionTests {
    @TestFactory
    public Stream<DynamicTest> of() {
        Map<String, SymantecVersion> tests = new LinkedHashMap<>();
        tests.put("0.0.0", new SymantecVersion(0, 0, 0));
        tests.put("1.2.3", new SymantecVersion(1, 2, 3));
        tests.put("V1.2.3", new SymantecVersion(1, 2, 3));
        tests.put("3.2.1", new SymantecVersion(3, 2, 1));
        tests.put("v3.2.1", new SymantecVersion(3, 2, 1));

        return tests.entrySet().stream()
                .map(e -> dynamicTest(e.getKey(), () -> {
                    final SymantecVersion expected = e.getValue();
                    final SymantecVersion actual = SymantecVersion.of(e.getKey());
                    assertEquals("major", expected.major, actual.major);
                    assertEquals("minor", expected.minor, actual.minor);
                    assertEquals("revision", expected.revision, actual.revision);
                }));
    }

    @Test
    public void incrementRevision() {
        SymantecVersion input = new SymantecVersion(1, 0, 0);
        SymantecVersion expected = new SymantecVersion(1, 0, 1);
        SymantecVersion actual = input.incrementRevision();
        assertEquals(expected, actual);
    }
}
