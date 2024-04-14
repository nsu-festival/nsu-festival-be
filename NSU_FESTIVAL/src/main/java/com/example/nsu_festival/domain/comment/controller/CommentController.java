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
@RequestMapping("/booths")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{boothId}/comment/posts")
    public ResponseEntity<StatusResponseDto> boothWriteComment(@RequestBody CommentDto commentDto, @PathVariable Long boothId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        if(    commentService.writeComment(commentDto,boothId,customOAuth2User)){
            return ResponseEntity.ok(StatusResponseDto.success(null));
            }
            return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
    }

    @PutMapping("/{boothId}/comment/{commentId}")
    public ResponseEntity<StatusResponseDto> updateComment( @PathVariable Long commentId, @RequestBody CommentUpdateDto commentUpdateDto
    ,@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        if(commentService.commentMatchUser(commentId,customOAuth2User)){
            if(commentService.updateComment(commentId,commentUpdateDto)){
                return ResponseEntity.ok(StatusResponseDto.success(null));
            }else {
                return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
            }
        }
        return ResponseEntity.status(403).body(StatusResponseDto.addStatus(403));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{boothId}/comment/{commentId}")
    public ResponseEntity<StatusResponseDto> deleteComment(@PathVariable Long commentId,@PathVariable Long boothId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        if(commentService.commentMatchUser(commentId,customOAuth2User)){
            commentService.deleteComment(commentId);
            return ResponseEntity.ok(StatusResponseDto.success(null));
        }
        return ResponseEntity.status(403).body(StatusResponseDto.addStatus(403));
    }

    /**
     * 댓글 신고
     */
        @PostMapping("/{boothId}/comment/{commentId}/report")
    public ResponseEntity<StatusResponseDto> reportComment(@PathVariable Long commentId, @PathVariable Long boothId, @RequestBody ReportCommentDto reportCommentDto){
       try{
           commentService.reportComment(commentId,reportCommentDto);
           return ResponseEntity.ok(StatusResponseDto.success(null));
       }catch (Exception e){
           return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
       }
       }

}
