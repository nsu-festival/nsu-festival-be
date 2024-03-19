package com.example.nsu_festival.domain.notice.service;

import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final UserRepository userRepository;
    @Override
    public boolean isAdmin(CustomOAuth2User customOAuth2User){
        String email = customOAuth2User.getEmail();
        if("admin".equals(userRepository.findRoleByEmail(email))){
            return true;
        }
        return false;
    }

}
