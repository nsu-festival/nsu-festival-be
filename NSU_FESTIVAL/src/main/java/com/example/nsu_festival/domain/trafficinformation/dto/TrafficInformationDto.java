package com.example.nsu_festival.domain.trafficinformation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrafficInformationDto {
    private String upFirstLocation;  // 상행선 첫 번째 도착 열차 위치
    private String upFirstTime;      // 상행선 첫 번째 도착 열차 시간
    private String upSecondLocation; // 상행선 두 번째 도착 열차 위치
    private String upSecondTime;     // 상행선 두 번째 도착 열차 시간
    private String dnFirstLocation;  // 하행선 첫 번째 도착 열차 위치
    private String dnFirstTime;      // 상행선 첫 번째 도착 열차 시간
    private String dnSecondLocation; // 하행선 두 번째 도착 열차 위치
    private String dnSecondTime;     // 하행선 첫 번째 도착 열차 시간
}
