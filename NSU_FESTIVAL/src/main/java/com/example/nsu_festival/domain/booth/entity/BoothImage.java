package com.example.nsu_festival.domain.booth.entity;

import jakarta.persistence.*;

@Entity
public class BoothImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boothImageId;

    @OneToOne(mappedBy = "booth",fetch = FetchType.LAZY)
    private Booth booth;
}
