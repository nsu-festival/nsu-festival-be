package com.example.nsu_festival.domain.trafficinformation.exception;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Component
@Slf4j
@AllArgsConstructor
public class TrafficInformationError {
    public String errorHandler(String errCode){
        int code = Integer.parseInt(errCode);
        if(code < 300){
            return "인증키 오류";
        }else if(code >= 300 && code <= 335) {
            return "요청값 오류";
        } else{
            return "서버 오류";
        }
    }
}
