package ma.mohcine.sms_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@SpringBootApplication
@Log4j2
public class SmsServiceApplication {


	public static void main(String[] args) {
		ConfigurableApplicationContext cac = 
			SpringApplication.run(SmsServiceApplication.class, args);

		cac.addApplicationListener(new ApplicationListener<ContextClosedEvent>(){

			@Override
			public void onApplicationEvent(ContextClosedEvent event) {
				log.info("cleaning active SSE connections ...");
				SseService sseService = cac.getBean(SseService.class);
				sseService.cleanUp();
				log.info("cleaned active SSE connections ...");
			}
			
		});
	}


}
