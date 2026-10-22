package dev.jkexamples.matrix;

/**
 * One project, three axes: a profile changed how this compiled, a feature chose whether gson
 * is on the classpath, and the build-type variant decides whether src-release/ adds the
 * ReleaseStamp class that Stamp reports.
 */
public final class Main {
    public static void main(String[] args) {
        String gson;
        try {
            Class.forName("com.google.gson.Gson");
            gson = "json feature ON";
        } catch (ClassNotFoundException e) {
            gson = "json feature OFF";
        }
        System.out.println("profiles-vs-variants: " + Stamp.describe() + ", " + gson);
    }
}
