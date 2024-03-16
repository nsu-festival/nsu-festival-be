package com.example.nsu_festival.domain.booth.dto;

import com.example.nsu_festival.domain.booth.entity.BoothCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllBoothDto {
    private Long boothId;
    private String title;
    private String area;
    private List<BoothCategory> boothCategories;
    private String boothImageUrl;
}
