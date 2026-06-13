package com.example.rasasumaterabackend.repository;

import com.example.rasasumaterabackend.model.Daerah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaerahRepository extends JpaRepository<Daerah, Long> {
    boolean existsByNama(String nama);
}