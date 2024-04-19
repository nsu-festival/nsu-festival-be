package com.example.nsu_festival.domain.likes.controller;

import com.example.nsu_festival.domain.likes.dto.UserLikeDto;
import com.example.nsu_festival.domain.likes.entity.ContentType;
import com.example.nsu_festival.domain.likes.service.BoothLikedServiceImpl;
import com.example.nsu_festival.domain.likes.service.DetermineServiceImpl;
import com.example.nsu_festival.domain.user.entity.User;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.RunnableScheduledFuture;

@Controller
@Slf4j
@AllArgsConstructor
@RequestMapping("likes/{contentType}")
public class LikeController {
    private final DetermineServiceImpl determineService;
    private final BoothLikedServiceImpl boothLikedService;
    @GetMapping("/days/{dDay}")
    ResponseEntity<StatusResponseDto> determineLikeContents(@PathVariable ContentType contentType, @PathVariable LocalDate dDay, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            if(determineService.isCorrectDate(dDay)){
                if(customOAuth2User == null){
                    List<UserLikeDto> userLikeDtoList = determineService.findNoUserLike(contentType, dDay);
                    return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDtoList));
                }
                if(determineService.determineUser(contentType, customOAuth2User)){
                    List<UserLikeDto> userLikeDtoList = determineService.findUserLike(contentType, customOAuth2User, dDay);
                    return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDtoList));
                }else{
                    determineService.createUserLike(contentType, customOAuth2User);
                    List<UserLikeDto> userLikeDtoList = determineService.findUserLike(contentType, customOAuth2User, dDay);
                    return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDtoList));
                }
            }
            return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
        } catch (RuntimeException e){
            return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
        }
    }
    @GetMapping("/{boothId}")
    ResponseEntity<StatusResponseDto> findBoothLiked(@PathVariable ContentType contentType, @PathVariable Long boothId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            if(determineService.determineUser(contentType, customOAuth2User)){
                UserLikeDto userLikeDto = boothLikedService.findBoothLike(boothId, customOAuth2User);
                return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDto));
            }else{
                boothLikedService.createBoothLike(customOAuth2User, boothId);
                UserLikeDto userLikeDto = boothLikedService.findBoothLike(boothId, customOAuth2User);
                return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDto));
            }
        }catch (RuntimeException e){
            return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
        }
    }
    @PostMapping("/{contentId}")
    ResponseEntity<StatusResponseDto> likeContents(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable ContentType contentType, @PathVariable Long contentId){
        try{
            if(determineService.determineContents(customOAuth2User, contentType, contentId)){
                return ResponseEntity.ok().body(StatusResponseDto.success());
            }
            return ResponseEntity.status(500).body(StatusResponseDto.addStatus(500));
        }catch (RuntimeException e){
            return ResponseEntity.status(400).body(StatusResponseDto.addStatus(400));
        }
    }
}
