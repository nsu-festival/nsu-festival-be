package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.likes.entity.FestivalProgramLiked;
import com.example.nsu_festival.domain.likes.entity.SingerLineupLiked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SingerLineupLikedRepository extends JpaRepository<SingerLineupLiked, Long> {
    @Query("select sll from SingerLineupLiked sll where sll.singerLineup.singerLineupId = :contentId")
    SingerLineupLiked findSingerLineupLikedByContentId(Long contentId);

    boolean existsByUserId(Long userId);

    @Query("select count(*) from SingerLineupLiked where singerLineup.singerLineupId = :singerLineupId and isSingerLineupLike = true")
    int countSingerLineupLike(Long singerLineupId);

    @Query("select sll from SingerLineupLiked sll where sll.user.id = :userId")
    List<SingerLineupLiked> findSingerLineupLikedListByUserId(Long userId);
}
