package com.example.nsu_festival.domain.likes.entity;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor
public class BoothLiked {
    @Id
    @NonNull
    @Column(name = "user_booth_like_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userBoothLikeId;

    private boolean isBoothLike;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "booth_id")
    private Booth booth;

    @Builder
    private BoothLiked(Long userBoothLikeId, boolean isBoothLike, User user, Booth booth){
        this.userBoothLikeId = userBoothLikeId;
        this.isBoothLike = isBoothLike;
        this.user = user;
        this.booth = booth;
    }


    public void updateBoothLiked(boolean isBoothLike){
        this.isBoothLike = isBoothLike;
    }
}
