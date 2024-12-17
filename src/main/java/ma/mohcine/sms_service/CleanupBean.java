package ma.mohcine.sms_service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CleanupBean implements DisposableBean{

    private final SseService sseService;

    @Override
    public void destroy() throws Exception {
        
        sseService.cleanUp();
    }
    
    

}
