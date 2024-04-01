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

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService{

    private final VisitorRepository visitorRepository;
    private static String ip;
    //프록시 또는 각 헤더 배열 선언
    private static final String[] headerTypes = {"Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "X-Forwarded-For"};

    @Transactional
    public void savedVisitor(HttpServletRequest request) {
        LocalDate now = LocalDate.now();
        String ipAddress = getRemoteAddr(request);
        //ip를 찾지 못했다면 종료
        if (ipAddress == null) {
            log.info("해당 세션 Id의 대한 ip를 찾지 못했습니다. : {}", request.getRequestedSessionId());
            return;
        }
        //찾은 ip가 저장되어 있는지 탐색
        Optional<Visitor> OptionalVisitor = visitorRepository.findByIpAddress(ipAddress);
        if (!OptionalVisitor.isPresent()) {
            log.info("새로운 ip 저장..");
            Visitor saveVisitor = Visitor.builder()
                    .ipAddress(ipAddress)
                    .visitTime(now)
                    .build();
            visitorRepository.save(saveVisitor);
            log.info("새로운 ip 저장 완료..");
            return;
        }

        Visitor findVisitor = OptionalVisitor.get();
        //저장된 ip일 때 과거 방문일아라면 방문 날짜 업데이트한다.
        if (!findVisitor.getVisitTime().equals(now)) {
            log.info("ip 방문 날짜 업데이트 시작..");
            findVisitor.updateTime(now);
            visitorRepository.save(findVisitor);
            log.info("ip 방문 날짜 업데이트 완료..");
        }
    }

    public String getRemoteAddr(HttpServletRequest request) {
        //헤더 타입을 통해 클라이언트 ip 추출
        for (String headerType : headerTypes) {
            ip = request.getHeader(headerType);
            if(ip != null) break;
        }
        //모든 헤더 ip가 없다면 remote Address 값을 가져온다.
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public VisitorResponseDto findVisitor() {
        //현재 날짜를 생성해 같은 방문일을 가진 ip 개수(==방문자 수)를 센다.
        LocalDate now = LocalDate.now();
        Long countByVisitor = visitorRepository.countByVisitTime(now);
        return new VisitorResponseDto(countByVisitor);
    }
}
