package com.example.nsu_festival.domain.comment.controller;


import com.example.nsu_festival.domain.booth.dto.BoothResponseDto;
import com.example.nsu_festival.domain.booth.dto.BoothResponseStatus;
import com.example.nsu_festival.domain.comment.dto.CommentDto;
import com.example.nsu_festival.domain.comment.dto.CommentUpdateDto;
import com.example.nsu_festival.domain.comment.service.CommentService;
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
    public ResponseEntity<BoothResponseDto> boothWriteComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
if(    commentService.writeComment(commentDto,customOAuth2User)){
    return ResponseEntity.status(200).body(BoothResponseDto.builder()
            .status(BoothResponseStatus.SUCCESS)
            .message("댓글 등록 완료")
            .data(null)
            .build());
}
    return ResponseEntity.status(400).body(BoothResponseDto.builder()
            .status(BoothResponseStatus.SUCCESS)
            .message("댓글 등록 실패")
            .data(null)
            .build());

    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<BoothResponseDto> updateComment( @PathVariable Long commentId, @RequestBody CommentUpdateDto commentUpdateDto
    ,@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        commentService.updateComment(commentId,commentUpdateDto);
        if(commentService.commentMatchUser(commentId,customOAuth2User)){
            commentService.updateComment(commentId,commentUpdateDto);
            return ResponseEntity.status(200)
                    .body(BoothResponseDto.builder()
                            .status(BoothResponseStatus.SUCCESS)
                            .message("내가 쓴 댓글 수정")
                            .data(null).build());
        }
        return ResponseEntity.status(400)
                .body(BoothResponseDto.builder()
                        .status(BoothResponseStatus.FAIL)
                        .message("회원님의 댓글이 아닙니다.")
                        .data(null)
                        .build());
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<BoothResponseDto> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        if(commentService.commentMatchUser(commentId,customOAuth2User)){
            commentService.deleteComment(commentId);
            return ResponseEntity.status(200)
                    .body(BoothResponseDto.builder()
                            .status(BoothResponseStatus.SUCCESS)
                            .message("내가 쓴 댓글 삭제")
                            .data(null)
                            .build());
        }
        return ResponseEntity.status(400)
                .body(BoothResponseDto.builder()
                        .status(BoothResponseStatus.FAIL)
                        .message("회원님의 댓글이 아닙니다.")
                        .data(null)
                        .build());
    }

}
