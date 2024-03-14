package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FestivalProgramLikedRepository extends JpaRepository<FestivalProgramLiked, Long> {
    @Query("select fpl from FestivalProgramLiked fpl where fpl.festivalProgram.festivalProgramId = :contentId")
    FestivalProgramLiked findFestivalProgramLikedByContentId(Long contentId);

    boolean existsByUserId(Long userId);

    @Query("select count(*) from FestivalProgramLiked where festivalProgram.festivalProgramId = :festivalProgramId and isFestivalProgramLike = true")
    int countFestivalProgramLike(Long festivalProgramId);

    @Query("select fql from FestivalProgramLiked fql where fql.user.id = :userId")
    List<FestivalProgramLiked> findFestivalProgramLikeListByUserId(Long userId);
}
