package com.example.nsu_festival.domain.visitor.service;

import com.example.nsu_festival.domain.visitor.dto.VisitorRequestDto;
import com.example.nsu_festival.domain.visitor.dto.VisitorResponseDto;
import com.example.nsu_festival.domain.visitor.entity.Visitor;
import com.example.nsu_festival.domain.visitor.repository.VisitorRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService{

    private final VisitorRepository visitorRepository;

    @Override
    @Transactional
    public String generateUUID() {
        LocalDate now = LocalDate.now();
        String newUUID = UUID.randomUUID().toString();
        newUUID += "-" + now;
        log.info("새로운 UUID 생성..");
        Visitor newVisitor = Visitor.builder()
                .UUID(newUUID)
                .visitTime(now)
                .build();

        visitorRepository.save(newVisitor);
        return newUUID;
    }

    @Override
    public VisitorResponseDto findVisitor() {
        //현재 날짜를 생성해 같은 방문일을 가진 ip 개수(==방문자 수)를 센다.
        LocalDate now = LocalDate.now();
        Long countByVisitor = visitorRepository.countByVisitTime(now);
        return new VisitorResponseDto(countByVisitor);
    }

    @Override
    public String validator(VisitorRequestDto visit) {
        String token = visit.getVisit();

        //널 값, 공백, 빈문자열 검증
        if(!StringUtils.hasText(token)){
            throw new IllegalArgumentException();
        }

        //첫 방문자라면 visit토큰 생성
        if("firstVisitor".equals(token)){
            return generateUUID();
        }

        return validate(token);
    }

    //입력 토큰이 정상일 때 실제 검증 메서드
    public String validate(String token){
        String now = LocalDate.now().toString();
        //유효한 토큰(금일 토큰) 또는 우리가 발급한 토큰이 맞는지 검증
        if(!visitorRepository.existsByUUID(token) || !now.equals(token.substring(37))){
            //조건 하나라도 부합한다면 새로 발급
            token = generateUUID();
        }

        return token;
    }
}
