package ma.mohcine.sms_service;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.Executor;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@SpringBootApplication

@Log4j2
public class SmsServiceApplication {

	

	public static void main(String[] args) {
		SpringApplication.run(SmsServiceApplication.class, args);
	}

	@Bean
	public GracefulShutdown gracefulShutdown(SseService sseService){
		return new GracefulShutdown(sseService);
	}

	@RequiredArgsConstructor
	private static class GracefulShutdown implements TomcatConnectorCustomizer,
            ApplicationListener<ContextClosedEvent> {


        private volatile Connector connector;

		private final SseService sseService;

        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {

			// start cleaning up
			log.info("cleaning up SSE connections ... ");
        	sseService.cleanUp();


            this.connector.pause();
            Executor executor = (Executor) this.connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                try (var threadPoolExecutor = (ThreadPoolExecutor) executor){
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                        log.warn("Tomcat thread pool did not shut down gracefully within "
                                + "30 seconds. Proceeding with forceful shutdown");
                    }
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    }

}
