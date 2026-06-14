package com.example.service;

import com.example.dto.StatistikResponse;
import com.example.model.Daerah;
import com.example.model.Kuliner;
import com.example.model.UlasanResponse;
import com.example.util.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final ObjectMapper objectMapper =  new ObjectMapper();

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

    // Tambah daerah
    public void addDaerah(String namaDaerah) throws IOException, InterruptedException {
        String json = "{\"nama\":\"" + namaDaerah + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/daerah"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

        client.send(request, HttpResponse.BodyHandlers.ofString()
        );
    }

    public void addKuliner(Kuliner kuliner, Long daerahId)
            throws IOException, InterruptedException {

        String json =
                objectMapper.writeValueAsString(kuliner);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL +
                                                "/kuliner?daerahId=" +
                                                daerahId
                                )
                        )
                        .header(
                                "Authorization",
                                "Bearer " + SessionManager.getToken()
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(json)
                        )
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 400) {
            throw new RuntimeException(
                    "Gagal menambah kuliner: " +
                            response.statusCode() +
                            "\n" +
                            response.body()
            );
        }
    }

    public void deleteKuliner(Long kulinerId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/kuliner?kulinerId=" + kulinerId))
                .header("Authorization", "Bearer " + SessionManager.getToken())
                .DELETE()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public StatistikResponse getStatistik() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/statistik"))
                .header("Authorization", "Bearer " + SessionManager.getToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), StatistikResponse.class);
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

    public boolean register(String nama, String email, String password) throws Exception {
        String endpoint = BASE_URL + "/auth/register";

        // 1. Menyusun Request Body dalam format JSON manual (Atau bisa memakai library Jackson/Gson)
        // Format payload disesuaikan dengan kebutuhan model di backend
        String jsonPayload = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                escapeJson(nama), escapeJson(email), escapeJson(password)
        );

        // 2. Membangun HTTP Request POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json") // Memberitahu backend bahwa kita mengirim JSON
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // 3. Mengirim Request secara Synchronous dan menerima Response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 4. Mengecek Status Code dari Backend
        // HTTP 201 Created menandakan entitas berhasil disimpan di DB H2 backend
        if (response.statusCode() == 201) {
            return true;
        } else {
            // Anda bisa mencetak response body jika ingin melakukan debugging alasan kegagalan dari backend
            System.err.println("Register Gagal. Status: " + response.statusCode() + " | Response: " + response.body());
            return false;
        }
    }

    public boolean login(String email, String password) throws Exception {
        // Abstraction: Menyusun payload JSON secara manual atau menggunakan library seperti Jackson/Gson
        String jsonPayload = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // Mengirim request secara synchronous
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            // Ekstraksi Token & Username dari JSON Response
            // Idealnya menggunakan library JSON parser (Jackson/Gson),
            // berikut trik regex/string manipulation simpel standar lab:
            String token = extractString(responseBody, "token");
            String username = extractString(responseBody, "username");

            // Simpan ke SessionManager jika berhasil
            SessionManager.setSession(token, username);
            return true;
        } else {
            // Jika backend mengirimkan pesan error validasi/keamanan
            String errorMessage = extractObject(response.body(), "message");
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Email atau Password salah!";
            }
            throw new RuntimeException(errorMessage);
        }
    }

    public void logout() throws Exception {

        String token = SessionManager.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/logout"))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Logout gagal. Status: " + response.statusCode()
            );
        }
    }

    // Ulasan
    public void kirimUlasan(Long kulinerId, String isiUlasan, int rating) throws Exception {

        String token = SessionManager.getToken();

        String json = String.format("""
        {
            "isiUlasan": "%s",
            "rating": %d
        }
        """, isiUlasan.replace("\"", "\\\""), rating);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/ulasan?kulinerId=" + kulinerId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Gagal mengirim ulasan: " + response.body()
            );
        }
    }

    public List<UlasanResponse> getUlasanByKuliner(Long kulinerId) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/ulasan?kulinerId=" + kulinerId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Gagal mengambil ulasan: " + response.body()
            );
        }

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(response.body(), new TypeReference<List<UlasanResponse>>() {});
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
        Long daerahId = extractLong(obj, "daerahId");
        Daerah daerah = new Daerah(daerahId, extractString(obj, "namaDaerah"));

        Kuliner k = new Kuliner(nama, daerah.toString(), deskripsi, imageUrl);
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

    /**
     * Helper Method untuk mencegah karakter ilegal merusak format string JSON (Escaping JSON)
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String s) {
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\/", "/");
    }
}
