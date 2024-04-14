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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class VisitorsTest {

    @Autowired
    VisitorServiceImpl visitorService;
    @Autowired
    VisitorRepository visitorRepository;

    static LocalDate now = LocalDate.now();


//    @BeforeEach
//    @DisplayName("기존 IP 세팅")
//    @Transactional
//    void savedIp(){
//        Visitor savedVisitor = Visitor.builder()
//                .UUID("120.0.0.1")
//                .visitTime(now)
//                .build();
//        visitorRepository.save(savedVisitor);
//    }

    @Test
    @DisplayName("방문자 uuid 중복 체크")
    @Transactional
    public void savedVisitor() {
        //given
        String uuid1 = UUID.randomUUID().toString();

        Visitor saveVisitor1 = Visitor.builder()
                .UUID(uuid1)
                .visitTime(now)
                .build();
        visitorRepository.save(saveVisitor1);
        String uuid2 = UUID.randomUUID().toString();

        Visitor saveVisitor2 = Visitor.builder()
                .UUID(uuid2)
                .visitTime(now)
                .build();
        visitorRepository.save(saveVisitor2);

        //when
        VisitorResponseDto count = visitorService.findVisitor();

        //then
        assertThat(count.getCount()).isEqualTo(2L);
        assertThat(uuid1).isNotEqualTo(uuid2);
    }
}
