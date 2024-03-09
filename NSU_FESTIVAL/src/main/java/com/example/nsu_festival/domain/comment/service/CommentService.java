package com.example.nsu_festival.domain.comment.service;

import com.example.nsu_festival.domain.comment.dto.CommentDto;
import com.example.nsu_festival.domain.comment.dto.CommentUpdateDto;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import org.springframework.stereotype.Service;

public interface CommentService {
    boolean writeComment(CommentDto commentDto, CustomOAuth2User customOAuth2User);
    void updateComment( Long commentId, CommentUpdateDto commentUpdateDto);
    boolean commentMatchUser(Long commentId, CustomOAuth2User customOAuth2User);
    void deleteComment(Long commentId);


}
