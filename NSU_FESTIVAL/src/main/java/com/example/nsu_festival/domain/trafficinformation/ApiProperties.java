package com.example.nsu_festival.domain.trafficinformation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.api")
/**
 * Api키를 가져오기 위한 클래스
 */
public class ApiProperties {

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
