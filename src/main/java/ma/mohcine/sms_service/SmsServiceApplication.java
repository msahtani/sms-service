package ma.mohcine.sms_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@SpringBootApplication
@Log4j2
public class SmsServiceApplication {


	public static void main(String[] args) {
		
		SpringApplication.run(SmsServiceApplication.class, args);

	
	}


}
