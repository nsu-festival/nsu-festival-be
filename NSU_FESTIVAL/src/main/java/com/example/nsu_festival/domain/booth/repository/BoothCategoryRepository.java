package com.example.nsu_festival.domain.booth.repository;

import com.example.nsu_festival.domain.booth.entity.BoothCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoothCategoryRepository extends JpaRepository<BoothCategory,Long> {
}
