package com.example.rasasumaterabackend.controller;

import com.example.rasasumaterabackend.model.Ulasan;
import com.example.rasasumaterabackend.service.UlasanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/ulasan")
public class UlasanController {

    @Autowired
    private UlasanService ulasanService;

    // 1. GET ULASAN BERDASARKAN KULINER (Bisa diakses umum tanpa login)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUlasan(@RequestParam Long kulinerId) {
        List<Map<String, Object>> response = new ArrayList<>();
        for (Ulasan u : ulasanService.getUlasanByKuliner(kulinerId)) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("isiUlasan", u.getIsiUlasan());
            map.put("rating", u.getRating());
            map.put("pengulas", u.getUsernamePengulas()); // Memanggil custom method kita di model
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // 2. POST ULASAN (Wajib Login)
    @PostMapping
    public ResponseEntity<?> kirimUlasan(@RequestParam Long kulinerId,
                                         @RequestBody Map<String, Object> requestBody) {
        try {
            // Mengambil data email dari konteks token JWT yang sedang aktif saat ini
            String emailContext = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String isiUlasan = (String) requestBody.get("isiUlasan");
            Integer rating = Integer.parseInt(requestBody.get("rating").toString());

            Ulasan saved = ulasanService.addUlasan(emailContext, kulinerId, isiUlasan, rating);
            return ResponseEntity.ok(Map.of(
                    "message", "Ulasan berhasil dikirim!",
                    "pengulas", saved.getUsernamePengulas()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}