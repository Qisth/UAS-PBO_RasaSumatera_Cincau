package com.example.rasasumaterabackend.controller;

import com.example.rasasumaterabackend.model.Daerah;
import com.example.rasasumaterabackend.model.Kuliner;
import com.example.rasasumaterabackend.service.DataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ContentController {

    @Autowired
    private DataService dataService;

    // 1. GET ALL DAERAH (Bisa diakses umum tanpa login)
    @GetMapping("/daerah")
    public ResponseEntity<List<Map<String, Object>>> listDaerah() {
        List<Map<String, Object>> response = new ArrayList<>();
        for (Daerah d : dataService.getAllDaerah()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("nama", d.getNama());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // 2. POST DAERAH (Hanya Admin)
    @PostMapping("/daerah")
    public ResponseEntity<?> tambahDaerah(@Valid @RequestBody Daerah daerah) {
        try {
            return ResponseEntity.ok(dataService.saveDaerah(daerah));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. GET ALL KULINER / SEARCH (Bisa diakses umum tanpa login)
    @GetMapping("/kuliner")
    public ResponseEntity<List<Map<String, Object>>> listKuliner(@RequestParam(required = false) String search) {
        List<Kuliner> kuliners = (search == null) ? dataService.getAllKuliner() : dataService.searchKuliner(search);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Kuliner k : kuliners) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", k.getId());
            map.put("nama", k.getNama());
            map.put("deskripsi", k.getDeskripsi());
            map.put("imageUrl", k.getImageUrl());
            map.put("daerahId", k.getDaerah().getId());
            map.put("namaDaerah", k.getDaerah().getNama());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // 4. POST KULINER (Hanya Admin - wajib menyertakan ID daerah lewat parameter / Query URL)
    @PostMapping("/kuliner")
    public ResponseEntity<?> tambahKuliner(@Valid @RequestBody Kuliner kuliner, @RequestParam Long daerahId) {
        try {
            Kuliner saved = dataService.saveKuliner(kuliner, daerahId);
            return ResponseEntity.ok(Map.of("message", "Kuliner berhasil ditambahkan!", "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}