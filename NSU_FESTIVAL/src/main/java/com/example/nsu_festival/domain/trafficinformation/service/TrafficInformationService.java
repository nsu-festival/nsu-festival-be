package com.example.nsu_festival.domain.trafficinformation.service;

import com.example.nsu_festival.domain.trafficinformation.ApiProperties;
import com.example.nsu_festival.domain.trafficinformation.dto.TrafficInformationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TrafficInformationService {
    private final ApiProperties apiProperties;

    /**
     *  실시간 지하철 정보 리스트 반환 메서드
     */
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
            String errCode = errorMessageNode.get("code").asText();

            errCode = errCode.chars()
                    .dropWhile(c -> c!='-')
                    .skip(1)
                    .filter(Character::isDigit)
                    .mapToObj(Character::toString)
                    .collect(Collectors.joining());

            log.error("ERROR_CODE: {}", errCode);

            if(!"000".equals(errCode)){
                throw new RuntimeException(errCode);
            }

            // 정상 응답 처리
            JsonNode realtimeArrivalList = rootNode.get("realtimeArrivalList");
            if (realtimeArrivalList == null || !realtimeArrivalList.isArray()) {
                log.warn("No arrival information found.");
                throw new NoSuchElementException();
            }

            List<TrafficInformationDto> trafficInformationDtoList = convertToDto(realtimeArrivalList);
            return trafficInformationDtoList;
        } catch (JsonProcessingException e) {
            log.error("JSON Processing Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * 프론트에게 전달할 Dto 변환 메서드
     */
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

            if('1' == ordKey.charAt(ordKey.length()-1)){                // 급행 여부
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
