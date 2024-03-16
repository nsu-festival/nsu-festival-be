package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.nsu_festival.domain.user.entity.User;

public interface BoothLikedRepository extends JpaRepository<BoothLiked,Long> {
    BoothLiked findBoothLikedByUser(User user);
    
    @Query("select count(*) from BoothLiked where booth.boothId = :festivalProgramId and isBoothLike = true")
    int countBoothLike(Long boothId);
}
