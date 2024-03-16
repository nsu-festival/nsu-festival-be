package com.example.nsu_festival.domain.likes.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLikeDto {
    private boolean isLike;         //좋아요 여부
    private String contentName;     //컨텐츠 이름
}
