package com.example.nsu_festival.domain.booth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JoinColumn(name = "booth_id",nullable = true)
    @JsonBackReference
    private Booth booth;
}
