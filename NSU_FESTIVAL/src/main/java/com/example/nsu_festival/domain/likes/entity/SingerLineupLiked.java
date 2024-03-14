package com.example.nsu_festival.domain.likes.entity;


import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import com.example.nsu_festival.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class SingerLineupLiked {
    @Id
    @NonNull
    @Column(name = "user_singerLineup_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userSingerLineupId;

    private boolean isSingerLineupLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singerLineup_id")
    private SingerLineup singerLineup;

    @Builder
    private SingerLineupLiked(Long userSingerLineupId, boolean isSingerLineupLike, User user, SingerLineup singerLineup){
        this.userSingerLineupId = userSingerLineupId;
        this.isSingerLineupLike = isSingerLineupLike;
        this.user = user;
        this.singerLineup = singerLineup;
    }

    public void updateSingerLineupLiked(boolean isSingerLineupLike){
        this.isSingerLineupLike = isSingerLineupLike;
    }
}
