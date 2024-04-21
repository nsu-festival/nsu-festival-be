package com.example.nsu_festival.global.config;

import com.example.nsu_festival.global.security.handler.CustomOAuth2AuthenticationFailureHandler;
import com.example.nsu_festival.global.security.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.example.nsu_festival.global.security.jwt.JwtAuthFilter;
import com.example.nsu_festival.global.security.jwt.JwtExceptionFilter;
import com.example.nsu_festival.global.security.oauth2.CustomClientRegistrationRepo;
import com.example.nsu_festival.global.security.service.CustomOAuth2UserService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistrationBean() {
        FilterRegistrationBean<JwtAuthFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(jwtAuthFilter);
        filterRegistrationBean.setEnabled(false); // 이 부분을 추가해줍니다.
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<JwtExceptionFilter> jwtExceptionFilterRegistrationBean() {
        FilterRegistrationBean<JwtExceptionFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(jwtExceptionFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }


    @Bean
    public WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                HttpMethod.GET,
                "/notices/{noticeId}", // GET 요청에 대해서만 규칙 적용
                "/visitors/**",
                "/booths/top",
                "/booths",
                "/trafficinformations",
                "/festivalprograms/days/{dDay}",
                "/singerlineups/days/{dDay}",
                "/notices",
                "/auth/reissue/access",
                "/auth/logout",
                "/booths/{boothId}/details"
        ).requestMatchers(
                HttpMethod.POST,
                "/booths/{boothId}/comment/{commentId}/repot"
        ).requestMatchers("/");
    }



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
                .oauth2Login((oauth2) -> oauth2
                    .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                    .successHandler(customOAuth2AuthenticationSuccessHandler)
                        .failureHandler(customOAuth2AuthenticationFailureHandler)
                    .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                        .userService(customOAuth2UserService)));
        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) ->auth
                        .requestMatchers(
                                HttpMethod.GET,
                                "/notices/{noticeId}", // GET 요청에 대해서만 규칙 적용
                                "/visitors/**",
                                "/booths/top",
                                "/booths",
                                "/trafficinformations",
                                "/festivalprograms/days/{dDay}",
                                "/singerlineups/days/{dDay}",
                                "/notices",
                                "/auth/reissue/access",
                                "/auth/logout",
                                "/booths/{boothId}/details"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/booths/{boothId}/comment/{commentId}/repot"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/booths/{boothId}/details/update"
                        ).permitAll()
                        .requestMatchers("/").permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/booths/{boothName}/details"
                        ).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/{boothId}/details/update"
                        ).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/notices/posts"
                        ).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/notices/{noticeId}"
                        ).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/notices/{noticeId}"
                                ).hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);

        return http.build();
    }


}

