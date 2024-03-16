package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoothLikedRepository extends JpaRepository<BoothLiked, Long> {
    @Query("select fpl from BoothLiked fpl where fpl.booth.boothId = :contentId")
    BoothLiked findBoothLikedByContentId(Long contentId);

    @Query("select count(*) from BoothLiked where booth.boothId = :festivalProgramId and isBoothLike = true")
    int countBoothLike(Long boothId);
}
