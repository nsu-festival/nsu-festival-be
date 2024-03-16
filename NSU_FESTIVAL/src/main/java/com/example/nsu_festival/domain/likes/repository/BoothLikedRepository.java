package com.example.nsu_festival.domain.likes.repository;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import com.example.nsu_festival.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoothLikedRepository extends JpaRepository<BoothLiked,Long> {
    BoothLiked findBoothLikedByUser(User user);
    BoothLiked findBoothLikedByBoothAndUser(Booth booth, User user);
}
