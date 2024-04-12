package com.example.nsu_festival.domain.visitor.service;

import com.example.nsu_festival.domain.visitor.dto.VisitorResponseDto;
import com.example.nsu_festival.domain.visitor.entity.Visitor;
import com.example.nsu_festival.domain.visitor.repository.VisitorRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        log.info("새로운 UUID 생성..");
        Visitor newVisitor = Visitor.builder()
                .UUID(newUUID)
                .visitTime(now)
                .build();

        visitorRepository.save(newVisitor);
        return newUUID;
    }

    public VisitorResponseDto findVisitor() {
        //현재 날짜를 생성해 같은 방문일을 가진 ip 개수(==방문자 수)를 센다.
        LocalDate now = LocalDate.now();
        Long countByVisitor = visitorRepository.countByVisitTime(now);
        return new VisitorResponseDto(countByVisitor);
    }
}
