package com.example.nsu_festival.domain.booth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class BoothImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boothImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id")
    private Booth booth;
}
