package com.example.nsu_festival;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class NsuFestivalApplicationTests {

	@Test
	void findTrafficInformation() {
		RestTemplate restTemplate = new RestTemplate();                                                                // 외부 Api호출을 위한 RestTemplate객체 생성
		String apiKey = "554c56466177686431333345437a5175";                                                                                        // api 키를 저장할 변수
		String api = "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimeStationArrival/0/5/성환";     // api 저장 변수

		String response = restTemplate.getForObject(api, String.class);
		System.out.println(response);
	}

}
