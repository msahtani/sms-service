package ma.mohcine.sms_service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class CleanupBean implements DisposableBean{

    private final SseService sseService;

    @Override
    public void destroy() throws Exception {
        log.info("cleaning up SSE connections ... ");
        sseService.cleanUp();
    }
    
    

}
