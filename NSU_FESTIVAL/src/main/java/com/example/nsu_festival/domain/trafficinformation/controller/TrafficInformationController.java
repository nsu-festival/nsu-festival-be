package com.example.nsu_festival.domain.trafficinformation.controller;

import com.example.nsu_festival.domain.trafficinformation.dto.TrafficInformationResponseDto;
import com.example.nsu_festival.domain.trafficinformation.exception.TrafficInformationError;
import com.example.nsu_festival.domain.trafficinformation.service.TrafficInformationService;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@AllArgsConstructor
public class TrafficInformationController {

    private TrafficInformationService trafficInformationService;
    private TrafficInformationError trafficInformationError;
    /**
     *  성환역 교통실시간 정보 리스트 전달
      */
    @GetMapping("/trafficinformations")
    public ResponseEntity<StatusResponseDto> findTrafficInformation(){
        try{
            List<TrafficInformationResponseDto> trafficInformationResponseDtoList = trafficInformationService.findTrafficInformation();
            return ResponseEntity.ok().body(StatusResponseDto.success(trafficInformationResponseDtoList));
        } catch (NoSuchElementException e){
            return ResponseEntity.ok().body(StatusResponseDto.fail("예정된 운행정보가 없습니다."));
        } catch (RuntimeException e){
            log.info("에러 코드(숫자) : {}", e.getMessage());
            int errCode = trafficInformationError.errorHandler(e.getMessage());
            log.info("에러 메시지 : {}", errCode);
            return ResponseEntity.status(errCode).body(StatusResponseDto.addStatus(errCode));
        }
    }
}
