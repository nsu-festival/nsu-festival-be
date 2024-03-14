package com.example.nsu_festival.domain.likes.entity;

import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import com.example.nsu_festival.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@NoArgsConstructor
@Getter
public class FestivalProgramLiked {
    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_festival_program_like_id")
    private Long userFestivalProgramLikeId;

    private boolean isFestivalProgramLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festivalProgram_id")
    private FestivalProgram festivalProgram;

    @Builder
    private FestivalProgramLiked(Long userFestivalProgramLikeId, boolean isFestivalProgramLike, User user, FestivalProgram festivalProgram){
        this.userFestivalProgramLikeId = userFestivalProgramLikeId;
        this.isFestivalProgramLike = isFestivalProgramLike;
        this.user = user;
        this.festivalProgram = festivalProgram;
    }

    public void updateFestivalProgramLiked(boolean isFestivalProgramLike){
        this.isFestivalProgramLike = isFestivalProgramLike;
    }
}
