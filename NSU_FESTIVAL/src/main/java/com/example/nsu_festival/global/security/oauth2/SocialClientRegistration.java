package com.example.nsu_festival.global.security.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

@Component
public class SocialClientRegistration {

    /**
     * 본 클래스는 변수 설정 파일이 아닌 클래스를 통해 소셜 로그인 제공 서비스에 대한 정보를 설정하기 위한 클래스
     * 특정 OAuth2 메서드 또는 사용자 메서드를 커스텀을 편리하게 하기 위해 클래스를 통해 정보 등록
     */


    // 카카오 소셜 로그인 정보 설정 메서드
    public ClientRegistration kakaoClientRegistration() {

        return ClientRegistration.withRegistrationId("kakao")
                .clientId("53fe495237c610ecf3993a2196f3dced")
                .clientSecret("sxalZ3ILnNx7XTqscsISR5KQUqHSLIaa")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientName("NsuFestival")
                .redirectUri("http://15.165.217.112:8080//login/oauth2/code/kakao")
                .scope("profile_nickname", "account_email")
                .issuerUri("https://kauth.kakao.com")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .build();
    }
}
