package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
import com.example.nsu_festival.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SingerLineupLikedRepository extends JpaRepository<SingerLineupLiked, Long> {

    boolean existsByUserId(Long userId);

    @Query("select count(*) from SingerLineupLiked where singerLineup.singerLineupId = :singerLineupId and isSingerLineupLike = true")
    int countSingerLineupLike(Long singerLineupId);

    SingerLineupLiked findByUserAndSingerLineup(User user, SingerLineup singerLineup);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SingerLineupLiked s WHERE s.singerLineup.singerLineupId = :singerLineupId")
    boolean existsByFestivalProgramId(@Param("singerLineupId") Long singerLineupId);
}
