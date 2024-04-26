package com.example.nsu_festival.booth;

import com.example.nsu_festival.domain.booth.service.BoothServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;


@Slf4j
@SpringBootTest
public class QueryResponseTimeTest {

    @Autowired
    BoothServiceImpl boothService;

    @Test
    @DisplayName("기본 전체 조회 시간 테스트")
    void allfind(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("캐싱 전 전체 조회 시작");
        boothService.getAllBooths();
        stopWatch.stop();
        log.info("캐싱 전 부스 리스트 전제 조회 시간 : {} 초", stopWatch.getTotalTimeSeconds());

        stopWatch = new StopWatch();
        stopWatch.start();
        log.info("캐싱 후 전체 조회 시작");
        boothService.getAllBooths();
        stopWatch.stop();
        log.info("캐싱 후 부스 리스트 전제 조회 시간 : {} 초", stopWatch.getTotalTimeSeconds());
    }
}
