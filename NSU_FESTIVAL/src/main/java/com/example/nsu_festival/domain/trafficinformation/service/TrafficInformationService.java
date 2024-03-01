package com.example.nsu_festival.domain.trafficinformation.service;

import com.example.nsu_festival.domain.trafficinformation.dto.TrafficInformationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@AllArgsConstructor
public class TrafficInformationService {
    public TrafficInformationDto findTrafficInformation() {
        RestTemplate restTemplate = new RestTemplate();
        String apiKey = ApiConfig.getApiKey();
        String api = "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimeStationArrival/0/5/성환";

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
                return null;
            }

            // 정상 응답 처리
            JsonNode realtimeArrivalList = rootNode.get("realtimeArrivalList");
            if (realtimeArrivalList == null || !realtimeArrivalList.isArray()) {
                log.warn("No arrival information found.");
                return null;
            }

            TrafficInformationDto trafficInformationDto = convertToDto(realtimeArrivalList);
            return trafficInformationDto;
        } catch (JsonProcessingException e) {
            log.error("JSON Processing Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private TrafficInformationDto convertToDto(JsonNode realtimeArrivalList) {
        TrafficInformationDto.TrafficInformationDtoBuilder builder = TrafficInformationDto.builder();
        for (JsonNode arrivalNode : realtimeArrivalList) {
            String updnLine = arrivalNode.get("updnLine").asText();
            String ordkey = arrivalNode.get("ordkey").asText().substring(0, 2); // 앞의 두 글자만 추출
            String arvlMsg2 = arrivalNode.get("arvlMsg2").asText();
            String arvlMsg3 = arrivalNode.get("arvlMsg3").asText();

            if ("상행".equals(updnLine)) {
                if ("01".equals(ordkey)) { // 앞의 두 글자가 "01"인지 비교
                    builder.upFirstLocation(arvlMsg3).upFirstTime(arvlMsg2);
                } else if ("02".equals(ordkey)) { // 앞의 두 글자가 "02"인지 비교
                    builder.upSecondLocation(arvlMsg3).upSecondTime(arvlMsg2);
                }
            } else if ("하행".equals(updnLine)) {
                if ("11".equals(ordkey)) { // 앞의 두 글자가 "11"인지 비교
                    builder.dnFirstLocation(arvlMsg3).dnFirstTime(arvlMsg2);
                } else if ("12".equals(ordkey)) { // 앞의 두 글자가 "12"인지 비교
                    builder.dnSecondLocation(arvlMsg3).dnSecondTime(arvlMsg2);
                }
            }
        }
        return builder.build();
    }

    @ConfigurationProperties(prefix = "api")
    private static class ApiConfig {
        private static String key;

        public static String getApiKey() {
            return key;
        }
    }
}
