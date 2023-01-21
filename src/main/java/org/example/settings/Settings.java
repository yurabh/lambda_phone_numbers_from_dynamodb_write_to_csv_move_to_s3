package org.example.settings;

public class Settings {

    private Settings() {
    }

    private static final String BUCKET_NAME = "BUCKET_NAME";

    private static final String KEY = "KEY";

    private static final String SOURCE_ACCESS_KEY = "SOURCE_ACCESS_KEY";

    private static final String SOURCE_SECRET_KEY = "SOURCE_SECRET_KEY";

    public static String getBucketName() {
        return getEnvVar(BUCKET_NAME);
    }

    public static String getAccessKey() {
        return getEnvVar(SOURCE_ACCESS_KEY);
    }

    public static String getSecretKey() {
        return getEnvVar(SOURCE_SECRET_KEY);
    }

    public static String getKey() {
        return getEnvVar(KEY);
    }

    private static String getEnvVar(String name) {
        return System.getenv(name);
    }
}
