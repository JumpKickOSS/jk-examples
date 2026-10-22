package dev.jkexamples.csvjson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.json.JsonMapper;

/** {@code csvjson FILE}: a CSV with a header row, printed as a JSON array of objects. */
public final class Main {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private Main() {}

    public static void main(String[] args) throws IOException {
        try (Reader in = args.length == 0
                ? new java.io.InputStreamReader(System.in, StandardCharsets.UTF_8)
                : Files.newBufferedReader(Path.of(args[0]))) {
            System.out.println(convert(in));
        }
    }

    static String convert(Reader csv) throws IOException {
        StringWriter all = new StringWriter();
        csv.transferTo(all);
        List<String> lines = all.toString().lines().filter(l -> !l.isBlank()).toList();
        if (lines.isEmpty()) return "[]";
        String[] header = lines.getFirst().split(",", -1);
        List<Map<String, String>> rows = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            String[] cells = line.split(",", -1);
            Map<String, String> row = new LinkedHashMap<>();
            for (int i = 0; i < header.length; i++) row.put(header[i].strip(), i < cells.length ? cells[i].strip() : "");
            rows.add(row);
        }
        return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(rows);
    }
}
