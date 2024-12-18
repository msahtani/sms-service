package ma.mohcine.sms_service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class GracefulShutdown implements TomcatConnectorCustomizer,
    ApplicationListener<ContextClosedEvent>{

    private static final int TIMEOUT = 30;

    private final SseService sseService;
    
    private volatile Connector connector;
    
    @Override
    public void onApplicationEvent(@SuppressWarnings("null") ContextClosedEvent event) {
        // start cleaning up
        log.info("cleaning up SSE connections ... ");
        sseService.cleanUp();


        this.connector.pause();
        Executor executor = this.connector.getProtocolHandler().getExecutor();
        if (executor instanceof ThreadPoolExecutor) {
            try (ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor){
                
                threadPoolExecutor.shutdown();
                if (!threadPoolExecutor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                    log.warn("Tomcat thread pool did not shut down gracefully within "
                            + "30 seconds. Proceeding with forceful shutdown");
                }

                threadPoolExecutor.shutdownNow();

                if (!threadPoolExecutor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                    log.error("Tomcat thread pool did not terminate");
                }
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    

}
