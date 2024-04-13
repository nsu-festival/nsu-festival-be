package com.example.nsu_festival.domain.visitor.dto;

import lombok.Getter;

@Getter
public class VisitorRequestDto {
    private String visit;

    public VisitorRequestDto(String visit) {
        this.visit = visit;
    }
}
