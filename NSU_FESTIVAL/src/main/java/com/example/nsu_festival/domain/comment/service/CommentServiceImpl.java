package com.example.nsu_festival.domain.comment.service;

import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.domain.booth.repository.BoothRepository;
import com.example.nsu_festival.domain.comment.dto.CommentDto;
import com.example.nsu_festival.domain.comment.dto.CommentUpdateDto;
import com.example.nsu_festival.domain.comment.dto.ReportCommentDto;
import com.example.nsu_festival.domain.comment.entity.Comment;
import com.example.nsu_festival.domain.comment.entity.Report;
import com.example.nsu_festival.domain.comment.repository.CommentRepository;
import com.example.nsu_festival.domain.comment.repository.ReportRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.exception.CustomException;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import com.vane.badwordfiltering.BadWordFiltering;
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
    private final ReportRepository reportRepository;


    /**
     *
     * 댓글 작성
     */
    @Transactional
    public boolean writeComment(CommentDto commentDto, CustomOAuth2User customOAuth2User){
        try{
            BadWordFiltering badWordFiltering = new BadWordFiltering();
              String badWord =  badWordFiltering.change(commentDto.getContent());

            User user = userRepository.findByEmail(customOAuth2User.getEmail()).get();
            Booth booth = boothRepository.findById(commentDto.getBoothId()).get();
            Comment comment = Comment.builder()
                            .content(badWord)
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
            BadWordFiltering badWordFiltering = new BadWordFiltering();
            String badWord =  badWordFiltering.change(commentUpdateDto.getContent());
            Comment comment = commentRepository.findById(commentId).get();
            comment.commentUpdate(badWord);

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

    /**
     * 댓글 신고
     */
    public  void reportComment(Long commentId, ReportCommentDto reportCommentDto){
        try{
            Comment comment = commentRepository.findById(commentId).get();
            User user = comment.getUser();
            comment.plusReportCount();
            Report report = Report.builder()
                    .reportReason(reportCommentDto.getReportReason())
                    .comment(comment)
                    .user(user)
                    .build();
            reportRepository.save(report);
            if(comment.getReportCount()>=10){
                comment.commentUpdate("신고 누적으로 인해 검열된 댓글입니다.");
            }
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }
    }

}
