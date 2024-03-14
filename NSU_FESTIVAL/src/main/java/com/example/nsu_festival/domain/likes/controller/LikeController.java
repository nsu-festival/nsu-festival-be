package com.example.nsu_festival.domain.likes.controller;

import com.example.nsu_festival.domain.likes.dto.UserLikeDto;
import com.example.nsu_festival.domain.likes.service.DetermineServiceImpl;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
@AllArgsConstructor
public class LikeController {
    private final DetermineServiceImpl determineService;
    @GetMapping("/api/like/{contentType}")
    ResponseEntity<StatusResponseDto> determineLikeContents(@PathVariable String contentType, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            if(determineService.determineUser(contentType, customOAuth2User)){
                List<UserLikeDto> userLikeDtoList = determineService.findUserLike(contentType, customOAuth2User);
                return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDtoList));
            }else{
                determineService.createUserLike(contentType, customOAuth2User);
                List<UserLikeDto> userLikeDtoList = determineService.findUserLike(contentType, customOAuth2User);
                return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDtoList));
            }
        } catch (RuntimeException e){
            log.info(e.getMessage());
            return ResponseEntity.status(400).body(StatusResponseDto.fail(e.getMessage()));
        }
    }

    @PostMapping("/api/like/{contentType}/{contentId}")
    ResponseEntity<StatusResponseDto> likeContents(@PathVariable String contentType, @PathVariable Long contentId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            if(determineService.determineContents(contentType, contentId)){
                return ResponseEntity.ok().body(StatusResponseDto.success());
            }
            return ResponseEntity.ok().body(StatusResponseDto.fail(400));
        }catch (RuntimeException e){
            log.info(e.getMessage());
            return ResponseEntity.ok().body(StatusResponseDto.fail(400));
        }
    }
}
