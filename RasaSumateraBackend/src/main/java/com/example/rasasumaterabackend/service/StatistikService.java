package com.example.rasasumaterabackend.service;

import com.example.rasasumaterabackend.dto.StatistikResponse;
import com.example.rasasumaterabackend.repository.DaerahRepository;
import com.example.rasasumaterabackend.repository.KulinerRepository;
import com.example.rasasumaterabackend.repository.UlasanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatistikService {

    @Autowired
    private KulinerRepository kulinerRepository;

    @Autowired
    private DaerahRepository daerahRepository;

    @Autowired
    private UlasanRepository ulasanRepository;

    public StatistikResponse getStatistik() {

        long totalKuliner =
                kulinerRepository.count();

        long totalProvinsi =
                daerahRepository.count();

        long totalUlasan =
                ulasanRepository.count();

        return new StatistikResponse(
                totalKuliner,
                totalProvinsi,
                totalUlasan
        );
    }
}