package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import com.example.nsu_festival.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FestivalProgramLikedRepository extends JpaRepository<FestivalProgramLiked, Long> {
    boolean existsByUserId(Long userId);

    @Query("select count(*) from FestivalProgramLiked where festivalProgram.festivalProgramId = :festivalProgramId and isFestivalProgramLike = true")
    int countFestivalProgramLike(Long festivalProgramId);

    FestivalProgramLiked findByUserAndFestivalProgram(User user, FestivalProgram festivalProgram);

    boolean existsByFestivalProgram(FestivalProgram festivalProgram);
}
