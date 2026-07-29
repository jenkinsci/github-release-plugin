package io.jenkins.plugins.github;

public class ParameterUtils {
    public static void checkArgument(String name, Object value) {
        if (null == value) {
            throw new IllegalArgumentException(String.format("'%s' must be set", name));
        }
    }
}
