package com.example.rasasumaterabackend.service;

import com.example.rasasumaterabackend.model.Daerah;
import com.example.rasasumaterabackend.model.Kuliner;
import com.example.rasasumaterabackend.repository.DaerahRepository;
import com.example.rasasumaterabackend.repository.KulinerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private DaerahRepository daerahRepository;

    @Autowired
    private KulinerRepository kulinerRepository;

    // --- LOGIKA DAERAH ---
    public List<Daerah> getAllDaerah() { return daerahRepository.findAll(); }

    public Daerah saveDaerah(Daerah daerah) {
        if(daerahRepository.existsByNama(daerah.getNama())) {
            throw new RuntimeException("Nama daerah sudah terdaftar!");
        }
        return daerahRepository.save(daerah);
    }

    // --- LOGIKA KULINER ---
    public List<Kuliner> getAllKuliner() { return kulinerRepository.findAll(); }

    public Kuliner getKulinerById(Long id) { return kulinerRepository.findById(id).orElse(null); }

    public List<Kuliner> getKulinerByDaerahId(Long daerahId) { return kulinerRepository.findByDaerahId(daerahId); }

    public List<Kuliner> searchKuliner(String query) {
        return kulinerRepository.findByNamaContainingIgnoreCase(query);
    }

    public Kuliner saveKuliner(Kuliner kuliner, Long daerahId) {
        Daerah daerah = daerahRepository.findById(daerahId)
                .orElseThrow(() -> new RuntimeException("Daerah tidak ditemukan!"));
        kuliner.setDaerah(daerah);
        return kulinerRepository.save(kuliner);
    }
}