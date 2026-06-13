package com.example.rasasumaterabackend.repository;

import com.example.rasasumaterabackend.model.Kuliner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KulinerRepository extends JpaRepository<Kuliner, Long> {
    // Fitur pencarian kuliner berdasarkan nama untuk kolom pencarian JavaFX
    List<Kuliner> findByNamaContainingIgnoreCase(String nama);

    List<Kuliner> findByDaerahId(Long daerahId);
}