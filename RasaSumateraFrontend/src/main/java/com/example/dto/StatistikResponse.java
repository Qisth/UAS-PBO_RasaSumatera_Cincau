package com.example.dto;

public class StatistikResponse {

    private long totalKuliner;
    private long totalProvinsi;
    private long totalUlasan;

    public StatistikResponse() {
    }

    public long getTotalKuliner() {
        return totalKuliner;
    }

    public long getTotalProvinsi() {
        return totalProvinsi;
    }

    public long getTotalUlasan() {
        return totalUlasan;
    }
}