package com.example.service;

import com.example.model.Daerah;
import com.example.model.Kuliner;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service untuk komunikasi dengan RasaSumateraBackend (Spring Boot REST API).
 * Menggunakan java.net.http.HttpClient (built-in JDK) dan parser JSON
 * sederhana berbasis regex agar tidak memerlukan dependency eksternal.
 */
public class ApiService {

    private static final String BASE_URL = "http://localhost:8080/api/v1";

    private final HttpClient client;

    public ApiService() {
        this.client = HttpClient.newHttpClient();
    }

    // ==========================================
    // 1. GET ALL DAERAH
    // ==========================================
    public List<Daerah> getAllDaerah() throws IOException, InterruptedException {
        String json = get("/daerah");
        List<Daerah> result = new ArrayList<>();
        for (String obj : splitJsonObjects(json)) {
            Long id = extractLong(obj, "id");
            String nama = extractString(obj, "nama");
            result.add(new Daerah(id, nama));
        }
        return result;
    }

    // ==========================================
    // 2. GET ALL KULINER / SEARCH BY NAMA
    // ==========================================
    public List<Kuliner> getAllKuliner() throws IOException, InterruptedException {
        return parseKulinerList(get("/kuliner"));
    }

    public List<Kuliner> searchKuliner(String keyword) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return parseKulinerList(get("/kuliner?search=" + encoded));
    }

    // ==========================================
    // 3. FILTER KULINER BERDASARKAN DAERAH ID
    // ==========================================
    public List<Kuliner> getKulinerByDaerah(Long daerahId) throws IOException, InterruptedException {
        return parseKulinerList(get("/kuliner/daerah/" + daerahId));
    }

    // ==========================================
    // 4. DETAIL KULINER BERDASARKAN ID
    // ==========================================
    public Kuliner getKulinerDetail(Long id) throws IOException, InterruptedException {
        String json = get("/kuliner/" + id);
        return parseKulinerDetail(json);
    }

    // ==========================================
    // Helper HTTP
    // ==========================================
    private String get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new IOException("Request gagal. Status: " + response.statusCode() + " - " + response.body());
        }
    }

    // ==========================================
    // Helper Parsing JSON (manual, ringan, tanpa library eksternal)
    // ==========================================
    private List<Kuliner> parseKulinerList(String json) {
        List<Kuliner> result = new ArrayList<>();
        for (String obj : splitJsonObjects(json)) {
            result.add(parseKulinerObject(obj));
        }
        return result;
    }

    private Kuliner parseKulinerDetail(String json) {
        return parseKulinerObject(json);
    }

    private Kuliner parseKulinerObject(String obj) {
        Long id = extractLong(obj, "id");
        String nama = extractString(obj, "nama");
        String deskripsi = extractString(obj, "deskripsi");
        String imageUrl = extractString(obj, "imageUrl");

        // namaDaerah bisa berada langsung (list/filter) atau di dalam objek nested "daerah" (detail)
        String namaDaerah = extractString(obj, "namaDaerah");
        Long daerahId = extractLong(obj, "daerahId");

        if (namaDaerah == null) {
            String daerahObj = extractObject(obj, "daerah");
            if (daerahObj != null) {
                namaDaerah = extractString(daerahObj, "nama");
                if (daerahId == null) {
                    daerahId = extractLong(daerahObj, "id");
                }
            }
        }

        Kuliner k = new Kuliner(nama, namaDaerah == null ? "" : namaDaerah, deskripsi, imageUrl);
        k.setId(id);
        k.setDaerahId(daerahId);
        return k;
    }

    /**
     * Memecah JSON array of objects "[{...},{...}]" menjadi list string "{...}"
     * dengan memperhatikan kedalaman kurung kurawal (mendukung objek nested).
     */
    private List<String> splitJsonObjects(String json) {
        List<String> result = new ArrayList<>();
        if (json == null) return result;

        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    result.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return result;
    }

    /**
     * Mengambil isi objek nested untuk key tertentu, misal: "daerah": {...}
     */
    private String extractObject(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\{[^{}]*\\})");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractString(String json, String key) {
        // Menangani null value: "key": null
        Pattern nullPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*null");
        if (nullPattern.matcher(json).find()) {
            return null;
        }

        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }
        return null;
    }

    private Long extractLong(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private String unescapeJson(String s) {
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\/", "/");
    }
}
