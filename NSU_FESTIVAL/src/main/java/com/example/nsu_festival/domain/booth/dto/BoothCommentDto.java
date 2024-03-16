package com.example.nsu_festival.domain.booth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoothCommentDto {
    private Long commentId;
    private String content;
    private String userName;
}
