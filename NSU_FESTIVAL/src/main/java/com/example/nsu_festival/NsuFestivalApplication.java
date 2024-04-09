package com.example.nsu_festival;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
		/*
		로컬에서 s3 연결시 발생하는 에러 로그 제거 로직
		 */
		exclude = {
				org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration.class,
				org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration.class
		}
)
@EnableScheduling
public class NsuFestivalApplication {

	public static void main(String[] args) {
		SpringApplication.run(NsuFestivalApplication.class, args);
	}

}
