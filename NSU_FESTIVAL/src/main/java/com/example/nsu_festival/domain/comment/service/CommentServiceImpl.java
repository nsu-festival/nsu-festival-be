package com.example.nsu_festival.domain.comment.service;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.comment.dto.CommentDto;
import com.example.nsu_festival.domain.comment.dto.CommentUpdateDto;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.comment.repository.CommentRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.exception.CustomException;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.example.nsu_festival.global.exception.ExceptionCode.SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final BoothRepository boothRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    /**
     *
     * 댓글 작성
     */
    @Transactional
    public boolean writeComment(CommentDto commentDto, CustomOAuth2User customOAuth2User){
        try{
            User user = userRepository.findByEmail(customOAuth2User.getEmail()).get();
            Booth booth = boothRepository.findById(commentDto.getBoothId()).get();
            Comment comment = Comment.builder()
                            .content(commentDto.getContent())
                                    .booth(booth)
                                            .user(user)
                    .build();
            commentRepository.save(comment);
            return true;
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }

    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long commentId, CommentUpdateDto commentUpdateDto){
        try{
            Comment comment = commentRepository.findById(commentId).get();
            comment.commentUpdate(commentUpdateDto);

        }catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }                                                      
    }


    /**
     *
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId){
        try {
            commentRepository.deleteById(commentId);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }
    }


    /**
     *
     * 내가 쓴 댓글 일치 여부 확인
     */
    public boolean commentMatchUser(Long commentId, CustomOAuth2User customOAuth2User){
        Comment comment = commentRepository.findById(commentId).get();
        User user  = userRepository.findByEmail(customOAuth2User.getEmail()).get();
        return comment.getUser().getId().equals(user.getId());

    }

}
