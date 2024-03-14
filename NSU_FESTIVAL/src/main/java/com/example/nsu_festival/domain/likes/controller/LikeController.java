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
import java.util.concurrent.RunnableScheduledFuture;

@Controller
@Slf4j
@AllArgsConstructor
public class LikeController {
    private final DetermineServiceImpl determineService;
    @GetMapping("/likes/{contentType}/days/{day}")
    ResponseEntity<StatusResponseDto> determineLikeContents(@PathVariable String contentType, @PathVariable int day,@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            if(determineService.determineUser(contentType, customOAuth2User)){
                List<UserLikeDto> userLikeDtoList = determineService.findUserLike(contentType, customOAuth2User);
                return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDtoList));
            }else{
                determineService.createUserLike(contentType, customOAuth2User);
                List<UserLikeDto> userLikeDtoList = determineService.findUserLike(contentType, customOAuth2User);
                return ResponseEntity.ok().body(StatusResponseDto.success(userLikeDtoList));
            }
        } catch (NullPointerException e) {
            List<UserLikeDto> userLikeDtoList = determineService.findNoUserLike(contentType);
            return ResponseEntity.status(401).body(StatusResponseDto.fail(userLikeDtoList));
        } catch (RuntimeException e){
            return ResponseEntity.status(400).body(StatusResponseDto.fail(e.getMessage()));
        }
    }
//    @GetMapping("/likes/no-access/{contentType}/days/{day}")
//    ResponseEntity<StatusResponseDto>
    @PostMapping("/likes/{contentType}/{contentId}")
    ResponseEntity<StatusResponseDto> likeContents(@PathVariable String contentType, @PathVariable Long contentId){
        try{
            if(determineService.determineContents(contentType, contentId)){
                return ResponseEntity.ok().body(StatusResponseDto.success());
            }
            return ResponseEntity.status(500).body(StatusResponseDto.fail(500));
        }catch (RuntimeException e){
            log.info(e.getMessage());
            return ResponseEntity.status(400).body(StatusResponseDto.fail(400));
        }
    }
}
