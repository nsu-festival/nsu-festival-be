package com.example.nsu_festival.domain.visitor.dto;

import lombok.Getter;

@Getter
public class VisitorResponseDto {
    private Long count;

    public VisitorResponseDto(Long count) {
        this.count = count;
    }
}
