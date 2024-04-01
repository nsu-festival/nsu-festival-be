package com.example.nsu_festival.visitors;

import com.example.nsu_festival.domain.visitor.dto.VisitorResponseDto;
import com.example.nsu_festival.domain.visitor.entity.Visitor;
import com.example.nsu_festival.domain.visitor.repository.VisitorRepository;
import com.example.nsu_festival.domain.visitor.service.VisitorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class VisitorsTest {

    @Autowired
    VisitorServiceImpl visitorService;
    @Autowired
    VisitorRepository visitorRepository;

    static LocalDate now = LocalDate.now();


    @BeforeEach
    @DisplayName("기존 IP 세팅")
    @Transactional
    void savedIp(){
        Visitor savedVisitor = Visitor.builder()
                .ipAddress("120.0.0.1")
                .visitTime(now)
                .build();
        visitorRepository.save(savedVisitor);
    }

    @Test
    @DisplayName("방문자 IP 중복 체크")
    @Transactional
    public void savedVisitor() {
        //given
        String ipAddress = "120.0.0.1";
        //찾은 ip가 저장되어 있는지 탐색
        Optional<Visitor> OptionalVisitor = visitorRepository.findByIpAddress(ipAddress);
        if (!OptionalVisitor.isPresent()) {
            Visitor saveVisitor = Visitor.builder()
                    .ipAddress(ipAddress)
                    .visitTime(now)
                    .build();
            visitorRepository.save(saveVisitor);
        } else {
            Visitor findVisitor = OptionalVisitor.get();
            //저장된 ip일 때 과거 방문일아라면 방문 날짜 업데이트한다.
            if (!findVisitor.getVisitTime().equals(now)) {
                findVisitor.updateTime(now);
                visitorRepository.save(findVisitor);
            }
        }

        String ipAddress2 = "120.0.0.2";
        //찾은 ip가 저장되어 있는지 탐색
        Optional<Visitor> OptionalVisitor2 = visitorRepository.findByIpAddress(ipAddress2);
        if (!OptionalVisitor2.isPresent()) {
            Visitor saveVisitor = Visitor.builder()
                    .ipAddress(ipAddress)
                    .visitTime(now)
                    .build();
            visitorRepository.save(saveVisitor);
        } else {
            Visitor findVisitor2 = OptionalVisitor2.get();
            //저장된 ip일 때 과거 방문일아라면 방문 날짜 업데이트한다.
            if (!findVisitor2.getVisitTime().equals(now)) {
                findVisitor2.updateTime(now);
                visitorRepository.save(findVisitor2);
            }
        }

        //when
        VisitorResponseDto count = visitorService.findVisitor();

        //then
        assertThat(count.getCount()).isEqualTo(2L);
        assertThat(count.getCount()).isNotEqualTo(3L);
    }
}
