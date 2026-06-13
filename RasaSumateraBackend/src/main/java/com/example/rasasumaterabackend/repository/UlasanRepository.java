package com.example.rasasumaterabackend.repository;

import com.example.rasasumaterabackend.model.Ulasan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UlasanRepository extends JpaRepository<Ulasan, Long> {
    // Mengambil semua ulasan yang terikat pada satu kuliner tertentu
    List<Ulasan> findByKulinerId(Long kulinerId);
}