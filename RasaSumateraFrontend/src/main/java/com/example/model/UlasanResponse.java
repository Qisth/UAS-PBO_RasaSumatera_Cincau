package com.example.model;

public class UlasanResponse {

    private Long id;
    private String isiUlasan;
    private Integer rating;
    private String pengulas;

    public UlasanResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsiUlasan() {
        return isiUlasan;
    }

    public void setIsiUlasan(String isiUlasan) {
        this.isiUlasan = isiUlasan;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getPengulas() {
        return pengulas;
    }

    public void setPengulas(String pengulas) {
        this.pengulas = pengulas;
    }
}