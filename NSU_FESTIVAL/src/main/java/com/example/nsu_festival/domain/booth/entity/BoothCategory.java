package com.example.nsu_festival.domain.booth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BoothCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boothCategoryId;

    private String category;

    @JsonBackReference //순환 참조 문제 해결하기 위한 어노테이션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id",nullable = false)
    private Booth booth;
}
