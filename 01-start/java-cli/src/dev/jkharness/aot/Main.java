package dev.jkharness.aot;

import com.google.gson.Gson;
import java.util.Map;

public final class Main {
    public static void main(String[] args) {
        // Enough class-loading surface for the AOT cache to matter.
        System.out.println(new Gson().toJson(Map.of("status", "ok", "aot", true)));
    }
}
