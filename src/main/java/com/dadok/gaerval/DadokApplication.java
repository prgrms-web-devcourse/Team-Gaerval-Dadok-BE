package com.dadok.gaerval;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import com.dadok.gaerval.global.common.JacocoExcludeGenerated;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DadokApplication {

	@JacocoExcludeGenerated
	public static void main(String[] args) {
		SpringApplication.run(DadokApplication.class, args);
	}

	@PostConstruct
	public void setUp(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
