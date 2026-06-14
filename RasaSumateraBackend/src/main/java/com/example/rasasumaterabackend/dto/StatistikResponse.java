package com.example.rasasumaterabackend.dto;

import lombok.Getter;

@Getter
public class StatistikResponse {

    private long totalKuliner;
    private long totalProvinsi;
    private long totalUlasan;

    public StatistikResponse(
            long totalKuliner,
            long totalProvinsi,
            long totalUlasan
    ) {
        this.totalKuliner = totalKuliner;
        this.totalProvinsi = totalProvinsi;
        this.totalUlasan = totalUlasan;
    }

}