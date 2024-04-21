package com.example.nsu_festival.domain.booth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoothDetailsRequestDto {

    private Long boothId;

    private String title;

    private String content;

    private String area;

    private String entryFee;

    private String boothName;

}
