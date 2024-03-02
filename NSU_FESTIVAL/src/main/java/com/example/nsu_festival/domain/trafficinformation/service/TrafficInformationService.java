package com.example.nsu_festival.domain.trafficinformation.service;

import com.example.nsu_festival.domain.trafficinformation.ApiProperties;
import com.example.nsu_festival.domain.trafficinformation.dto.TrafficInformationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TrafficInformationService {
    private final ApiProperties apiProperties;
    public List<TrafficInformationDto> findTrafficInformation() {
        RestTemplate restTemplate = new RestTemplate();
        String apiKey = apiProperties.getKey();
        String api = "http://swopenapi.seoul.go.kr/api/subway/"+apiKey+"/json/realtimeStationArrival/0/5/성환";
        try {
            String response = restTemplate.getForObject(api, String.class);
            log.info(response);
            // JSON 응답 처리
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode errorMessageNode = rootNode.get("errorMessage");

            // 에러 메시지가 있는지 확인
            if (errorMessageNode != null) {
                // 에러 메시지 처리
                String message = errorMessageNode.get("message").asText();
                log.error("API Error: {}", message);
            }

            // 정상 응답 처리
            JsonNode realtimeArrivalList = rootNode.get("realtimeArrivalList");
            if (realtimeArrivalList == null || !realtimeArrivalList.isArray()) {
                log.warn("No arrival information found.");
                return null;
            }

            List<TrafficInformationDto> trafficInformationDtoList = convertToDto(realtimeArrivalList);
            return trafficInformationDtoList;
        } catch (JsonProcessingException e) {
            log.error("JSON Processing Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<TrafficInformationDto> convertToDto(JsonNode realtimeArrivalList) {
        List<TrafficInformationDto> trafficInformationDtoList = new ArrayList<>();
        for (JsonNode arrivalNode : realtimeArrivalList) {
            String updnLine = arrivalNode.get("updnLine").asText();     //상하행 여부
            String ordKey = arrivalNode.get("ordkey").asText();         //열차운행 정보 코드, 급행인지 아닌지 판단하기 위함
            String arvlMsg2 = arrivalNode.get("arvlMsg2").asText()      //성환역까지 남은 역
                    .replace("[", "")
                    .replace("]", "")
                    .split("\\(")[0]
                    .trim();
            String arvlMsg3 = arrivalNode.get("arvlMsg3").asText();     //현재 열차 위치

            if('1' == ordKey.charAt(ordKey.length()-1)){
                arvlMsg2 = arvlMsg2 + "(급)";
            }

            TrafficInformationDto trafficInformationDto = TrafficInformationDto.builder()
                    .updnLine(updnLine)
                    .arrivalLocation(arvlMsg3)
                    .arrivalTime(arvlMsg2)
                    .build();
            trafficInformationDtoList.add(trafficInformationDto);
        }
        return trafficInformationDtoList;
    }
}
