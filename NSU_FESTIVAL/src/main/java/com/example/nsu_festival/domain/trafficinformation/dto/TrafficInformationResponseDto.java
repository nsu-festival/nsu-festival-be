package com.example.nsu_festival.domain.trafficinformation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrafficInformationResponseDto {
    private String updnLine;        // 열차 상하행 여부
    private String arrivalLocation; // 열차 실시간 도착역
    private String arrivalTime;     // 열차가 성환역까지 도착할때까지 남은 역
}
