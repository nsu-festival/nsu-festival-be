package com.example.nsu_festival.domain.booth.dto;

import com.example.nsu_festival.domain.booth.entity.BoothCategory;
import com.example.nsu_festival.domain.booth.entity.BoothImage;
import com.example.nsu_festival.domain.booth.entity.Menu;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.likes.entity.BoothLiked;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoothDetailDto {
    private Long boothId;
    private String title;
    private String content;
    private int countLike;
    private String area;
    private List<BoothCategory> boothCategories;
    private BoothImage boothImage;

    private List<BoothCommentDto> comments;
    private BoothLiked boothLiked;
    private Long boothCommentCount;
    private List<Menu> menus;
}
