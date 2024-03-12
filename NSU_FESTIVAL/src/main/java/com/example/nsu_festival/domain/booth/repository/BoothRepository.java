package com.example.nsu_festival.domain.booth.repository;

import com.example.nsu_festival.domain.booth.entity.Booth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoothRepository extends JpaRepository<Booth,Long> {
}

