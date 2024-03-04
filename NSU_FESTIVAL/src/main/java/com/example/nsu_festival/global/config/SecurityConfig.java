package com.example.nsu_festival.global.config;

import com.example.nsu_festival.global.security.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.example.nsu_festival.global.security.jwt.JwtAuthFilter;
import com.example.nsu_festival.global.security.jwt.JwtExceptionFilter;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import com.example.nsu_festival.global.security.oauth2.CustomClientRegistrationRepo;
import com.example.nsu_festival.global.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        //csrf disable
        http
                .csrf((auth) -> auth.disable());
        //Form login disable
        http
                .formLogin((auth) -> auth.disable());
        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //oauth2
        http
//                .oauth2Login((oauth2) -> oauth2
//                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
//                                .userService(customOAuth2UserService)));
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                    .loginPage("/login")
                    .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                    .successHandler(customOAuth2AuthenticationSuccessHandler)
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)));
        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) ->auth
                        .requestMatchers("/login/**", "/", "/token/**").permitAll()
                        .anyRequest().authenticated());

        http
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            String username = customOAuth2User.getUsername();
            String body = "{\"customOAuth2User\":\"" + customOAuth2User.getName()+ "\"}";


            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            try (PrintWriter writer = response.getWriter()) {
                writer.println(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
