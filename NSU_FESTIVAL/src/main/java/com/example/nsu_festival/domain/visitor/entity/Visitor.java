package com.example.nsu_festival.domain.visitor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UUID", nullable = false)
    private String UUID;

    @Column(name = "visitTime", nullable = false)
    private LocalDate visitTime;


    public void updateTime(LocalDate now) {
        this.visitTime = now;
    }

}
