package com.example.nsu_festival.domain.comment.controller;


import com.example.nsu_festival.domain.comment.dto.CommentDto;
import com.example.nsu_festival.domain.comment.dto.CommentUpdateDto;
import com.example.nsu_festival.domain.comment.dto.ReportCommentDto;
import com.example.nsu_festival.domain.comment.service.CommentService;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("booth/comment")
    public ResponseEntity<StatusResponseDto> boothWriteComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        if(    commentService.writeComment(commentDto,customOAuth2User)){
            return ResponseEntity.ok(StatusResponseDto.success(null));
            }
            return ResponseEntity.ok(StatusResponseDto.addStatus(404));

    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<StatusResponseDto> updateComment( @PathVariable Long commentId, @RequestBody CommentUpdateDto commentUpdateDto
    ,@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        commentService.updateComment(commentId,commentUpdateDto);
        if(commentService.commentMatchUser(commentId,customOAuth2User)){
            commentService.updateComment(commentId,commentUpdateDto);
            return ResponseEntity.ok(StatusResponseDto.success(null));
        }
        return ResponseEntity.ok(StatusResponseDto.addStatus(404));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<StatusResponseDto> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        if(commentService.commentMatchUser(commentId,customOAuth2User)){
            commentService.deleteComment(commentId);
            return ResponseEntity.ok(StatusResponseDto.success(null));
        }
        return ResponseEntity.ok(StatusResponseDto.addStatus(404));
    }

    /**
     * 댓글 신고
     */
        @PostMapping("report/comment/{commentId}")
    public ResponseEntity<StatusResponseDto> reportComment(@PathVariable Long commentId, @RequestBody ReportCommentDto reportCommentDto){
       try{
           commentService.reportComment(commentId,reportCommentDto);
           return ResponseEntity.ok(StatusResponseDto.success(null));
       }catch (Exception e){
           return ResponseEntity.ok(StatusResponseDto.addStatus(404));
       }
       }

}
