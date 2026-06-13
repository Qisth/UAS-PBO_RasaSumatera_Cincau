package com.example.rasasumaterabackend.service;

import com.example.rasasumaterabackend.model.Kuliner;
import com.example.rasasumaterabackend.model.Ulasan;
import com.example.rasasumaterabackend.model.User;
import com.example.rasasumaterabackend.repository.KulinerRepository;
import com.example.rasasumaterabackend.repository.UlasanRepository;
import com.example.rasasumaterabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UlasanService {

    @Autowired
    private UlasanRepository ulasanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KulinerRepository kulinerRepository;

    public List<Ulasan> getUlasanByKuliner(Long kulinerId) {
        return ulasanRepository.findByKulinerId(kulinerId);
    }

    public Ulasan addUlasan(String emailContext, Long kulinerId, String isiUlasan, Integer rating) {
        User user = userRepository.findByEmail(emailContext)
                .orElseThrow(() -> new RuntimeException("User tidak valid!"));

        Kuliner kuliner = kulinerRepository.findById(kulinerId)
                .orElseThrow(() -> new RuntimeException("Kuliner tidak ditemukan!"));

        Ulasan ulasan = new Ulasan(user, isiUlasan, rating, kuliner);
        return ulasanRepository.save(ulasan);
    }
}