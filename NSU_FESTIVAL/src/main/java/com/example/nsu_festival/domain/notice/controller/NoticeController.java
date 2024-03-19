package com.example.nsu_festival.domain.notice.controller;

import com.example.nsu_festival.domain.notice.dto.NoticeRequestDto;
import com.example.nsu_festival.domain.notice.service.NoticeService;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;
    @GetMapping("")
    public ResponseEntity<StatusResponseDto> findAllNoticeList(){
        return ResponseEntity.ok().body(StatusResponseDto.success());
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<StatusResponseDto> findNoticeDetail(@PathVariable Long noticeId){
        return ResponseEntity.ok().body(StatusResponseDto.success());
    }

    /**
     *  공지사항 작성
     */
    @PostMapping("/posts")
    public ResponseEntity<StatusResponseDto> writeNotice(@Valid @RequestBody NoticeRequestDto noticeRequestDto, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            noticeService.writeNotice(noticeRequestDto, customOAuth2User);
            return ResponseEntity.ok().body(StatusResponseDto.success());
        }catch (HttpClientErrorException e){
            return ResponseEntity.status(401).body(StatusResponseDto.addStatus(401));
        }catch (RuntimeException e){
            return ResponseEntity.status(500).body(StatusResponseDto.addStatus(500));
        }
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<StatusResponseDto> updateNotice(@PathVariable Long noticeId, @Valid @RequestBody NoticeRequestDto noticeRequestDto, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            noticeService.updateNotice(noticeId, noticeRequestDto, customOAuth2User);
            return ResponseEntity.ok().body(StatusResponseDto.success());
        }catch (HttpClientErrorException e){
            return ResponseEntity.status(401).body(StatusResponseDto.addStatus(401));
        }catch (RuntimeException e){
            return ResponseEntity.status(500).body(StatusResponseDto.addStatus(500));
        }
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<StatusResponseDto> deleteNotice(@PathVariable Long noticeId){
        return ResponseEntity.ok().body(StatusResponseDto.success());
    }
}
