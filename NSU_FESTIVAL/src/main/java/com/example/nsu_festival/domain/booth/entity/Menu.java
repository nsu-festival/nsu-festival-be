package com.example.nsu_festival.domain.booth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    private String name;
    private String price;

    @JsonBackReference //순환 참조 문제 해결하기 위한 어노테이션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id",nullable = false)
    private Booth booth;
}
