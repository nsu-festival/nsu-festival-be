package com.example.nsu_festival.domain.trafficinformation.controller;

import com.example.nsu_festival.domain.trafficinformation.dto.TrafficInformationDto;
import com.example.nsu_festival.domain.trafficinformation.service.TrafficInformationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@AllArgsConstructor
public class TrafficInformationController {

    private TrafficInformationService trafficInformationService;

    @GetMapping("/api")
    public ResponseEntity<?> findTrafficInformation(){
        List<TrafficInformationDto> trafficInformationDtoList = trafficInformationService.findTrafficInformation();
        return ResponseEntity.ok().body(trafficInformationDtoList);
    }
}
