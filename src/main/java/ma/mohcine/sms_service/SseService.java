package ma.mohcine.sms_service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;


import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SseService {

    private final Map<String, SseEmitter> sses;

    public SseService() {
        this.sses = new HashMap<>();
    }


    public SseEmitter register(String phoneNumber) throws IOException {
        
        
        String key = Optional
            .ofNullable(phoneNumber)
            .orElse(UUID.randomUUID().toString());

        final SseEmitter emitter = new SseEmitter(0L);

       

        // on error -> copmplete
        emitter.onError(ex -> {
            log.error("error: {}", ex.getMessage());
            emitter.complete();
        });
        
        // on completion
        emitter.onCompletion(() -> {
            log.info(
                "SSE connection is completed from the server : {}",
                phoneNumber
            );
            sses.remove(key);
        });

         // establish connection
         emitter.send(
            event().name("establish").data("connected successfully")
         );
            
        sses.put(key, emitter);
        
        return emitter;
    }

    public void send(SMS sms) throws IOException {

        if(sses.size() == 0){
            log.warn("There are no connected device");
            return;
        }

        if(sses.size() == 1){
            log.warn("there is 1 connected device, ignoring 'sender' attribute");
           
            for(SseEmitter sse: sses.values()){
                sse.send(sms);
            }
            return;
        }

        Optional.ofNullable(sses.get(sms.getSender()))
            .ifPresentOrElse(
                sse -> {
                    try{
                        sse.send(sms);
                    }catch(Exception ex){
                        throw new RuntimeException(ex.getMessage());
                    }
                },
                () -> {
                    throw new RuntimeException("the device is not connected");
                }
            );
        

    }

    public void cleanUp(){
        for(SseEmitter sse: sses.values()){
            sse.complete();
        }
        sses.clear();
    }

}
