package com.example.rasasumaterabackend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class BaseEntity {

    // Encapsulation: Getter dan Setter untuk ID yang diwariskan
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}