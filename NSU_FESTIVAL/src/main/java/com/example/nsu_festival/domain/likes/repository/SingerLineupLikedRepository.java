package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
import com.example.nsu_festival.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SingerLineupLikedRepository extends JpaRepository<SingerLineupLiked, Long> {

    boolean existsByUserId(Long userId);

    @Query("select count(*) from SingerLineupLiked where singerLineup.singerLineupId = :singerLineupId and isSingerLineupLike = true")
    int countSingerLineupLike(Long singerLineupId);

    SingerLineupLiked findByUserAndSingerLineup(User user, SingerLineup singerLineup);

    boolean existsBySingerLineup(SingerLineup singerLineup);
}
