package com.example.nsu_festival.domain.booth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoothResponseDto<T> {
    private BoothResponseStatus status;
    private String message;
    private T data;

}
