package com.example.nsu_festival.domain.likes.entity;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table
public class BoothLiked {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userBoothLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference  // 순환참조를 막기 위한 어노테이션,자식클래스
    @JoinColumn(name = "booth_id",nullable = false)
    private Booth booth;
}
