package com.example.nsu_festival.global.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean // 메소드의 실행 결과로 반환된 객체를 스프링의 빈으로 등록
    public ModelMapper getMapper(){         //modelMapper를 반환하는 메소드
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)  // LOOSE 전략 사용으로 필드 이름과 유형이 완전히 일치하지 않더라도 매핑을 시도
                .setSkipNullEnabled(true)       // 매핑 중 null인 필드는 skip
                .setFieldMatchingEnabled(true)  // private으로 선언된 인스턴스 변수에 접근 허용
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE); //Access level
        return modelMapper;

    }
}
