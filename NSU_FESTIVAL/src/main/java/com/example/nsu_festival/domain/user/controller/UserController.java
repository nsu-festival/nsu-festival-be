package com.example.nsu_festival.domain.user.controller;

import com.example.nsu_festival.domain.user.service.UserService;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/name")
    public ResponseEntity<StatusResponseDto> getUserName(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        String userName = userService.findUserName(customOAuth2User);
        if (!"Unknown".equals(userName)) {
            return ResponseEntity.ok(StatusResponseDto.success(userName));
        }
        return ResponseEntity.status(401).body(StatusResponseDto.addStatus(401));
    }
    @GetMapping("/role")
    public ResponseEntity<StatusResponseDto> getUserRole(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        String userRole = userService.findUserRole(customOAuth2User);
        if(!"Unknown".equals(userRole)){
            return ResponseEntity.ok(StatusResponseDto.success(userRole));
        }
        return ResponseEntity.status(401).body(StatusResponseDto.addStatus(401));
    }
}
