package com.example.nsu_festival.global.security.service;

import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import com.example.nsu_festival.global.security.dto.KakaoResponse;
import com.example.nsu_festival.global.security.dto.OAuth2Response;
import com.example.nsu_festival.global.security.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRespository;
    private static final String[] adminEmails = new String[] {"qkrals2475@daum.net", "whdkqls122@naver.com",
            "a0507013@gmail.com", "ahl5403@naver.com", "jjmj3434@gmail.com",
            "ssm000104@hanmail.net", "tjdah0850@naver.com", "dudxo0623@nate.com"};


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;       // 구글 또는 네이버 등 확장성을 염두해 부모 타입으로 구현
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }


        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();      // 특정인 지칭
        User existData = userRespository.findByUserName(username);
        UserDTO userDTO = new UserDTO();

        if (existData == null) {

            if (validateAdminEmail(oAuth2Response)) {
                User newUser = User.builder()
                        .userName(username)
                        .nickName(oAuth2Response.getName())
                        .email(oAuth2Response.getEmail())
                        .role("ROLE_ADMIN")
                        .build();
                userRespository.save(newUser);
                userDTO.setUsername(username);
                userDTO.setName(oAuth2Response.getName());
                userDTO.setEmail(oAuth2Response.getEmail());
                userDTO.setRole("ROLE_ADMIN");
            }
            else {

                User newUser = User.builder()
                        .userName(username)
                        .nickName(oAuth2Response.getName())
                        .email(oAuth2Response.getEmail())
                        .role("ROLE_USER")
                        .build();
                userRespository.save(newUser);
                userDTO.setUsername(username);
                userDTO.setName(oAuth2Response.getName());
                userDTO.setEmail(oAuth2Response.getEmail());
                userDTO.setRole("ROLE_USER");
            }
        } else {
            existData.userUpdate(oAuth2Response.getName(), oAuth2Response.getEmail());  // 유저 정보는 닉네임과 이메일만 변경 될 수 있으니 해당되는 2가지만 업데이트

            userRespository.save(existData);

            userDTO.setUsername(existData.getUserName());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setRole(existData.getRole());
        }
        return new CustomOAuth2User(userDTO);
    }

    private boolean validateAdminEmail(OAuth2Response oAuth2Response){
        for (String adminEmail : adminEmails) {
            if (adminEmail.equals(oAuth2Response.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
