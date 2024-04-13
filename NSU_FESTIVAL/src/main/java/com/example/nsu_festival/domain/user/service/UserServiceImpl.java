package com.example.nsu_festival.domain.user.service;

import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public String findUserName(CustomOAuth2User customOAuth2User) {
        //로그인 정보를 통해 얻은 이메일(고유 값)을 꺼내서
        String email = customOAuth2User.getEmail();
        //DB에 저장되어 있는 유저가 맞는지 검증 후 검증 성공 시 유저 이름 반환
        if (userRepository.existsByEmail(email)) {
            log.info("사용자 이름 획득 성공!");
            return customOAuth2User.getName();
        }
        //검증 실패 시 알려지지 않은 사용자인 Unknown 문자열을 반환
        return "Unknown";
    }

    @Override
    public String findUserRole(CustomOAuth2User customOAuth2User) {
        String email = customOAuth2User.getEmail();
        //DB에 저장된 유저가 맞는지 검증 후 성공하면 유저 역할 반환
        if(userRepository.existsByEmail(email)){
            log.info("사용자 역할 획득 성공!");
            return userRepository.findRoleByEmail(email);
        }
        //검증 실패 시 알려지지 않은 사용자인 Unknown 문자열을 반환
        return "Unknown";
    }
}
