package com.example.nsu_festival.domain.booth.repository;

import com.example.nsu_festival.domain.booth.entity.Booth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoothRepository extends JpaRepository<Booth,Long> {
    @Query(value = "SELECT *, RANK() OVER (ORDER BY count_like DESC) AS booth_rank FROM booth ORDER BY count_like DESC LIMIT 5", nativeQuery = true)
    List<Booth> findTopBoothByCountLike();

}

