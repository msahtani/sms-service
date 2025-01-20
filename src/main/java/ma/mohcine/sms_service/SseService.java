package ma.mohcine.sms_service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks.Many;
import static reactor.core.publisher.Sinks.many;


@Service
@Log4j2
public class SseService {

    private final Map<String, Many<SMS>> sinks;

    public SseService() {
        this.sinks = new HashMap<>();
    }


    public Flux<SMS> register(String phoneNumber) throws IOException {
        
        
        String key = Optional
            .ofNullable(phoneNumber)
            .orElse(UUID.randomUUID().toString());


        Many<SMS> sink = many().multicast().onBackpressureBuffer();

    
        // establish connection
        // emitter.send(
        //     event().name("establish").data("connected successfully")
        // );

        sinks.put(key, sink);
    
        
        return sink.asFlux()
            .doOnError(ex -> {
                log.error("error: {}", ex.getMessage());
            }).doOnCancel(() -> {
                log.info("SSE connection is cancelled from the server : {}", key);
            }).doOnComplete(() -> {
                log.info(
                    "SSE connection is completed from the server : {}",
                    phoneNumber
                );
                sinks.remove(key);
            });

    }

    public void send(SMS sms) throws IOException {

        if(sinks.size() == 0){
            log.warn("There are no connected device");
            return;
        }

        if(sinks.size() == 1){
            log.warn("there is 1 connected device, ignoring 'sender' attribute");
           
            for(Many<SMS> sink: sinks.values()){
                sink.tryEmitNext(sms);
            }
            return;
        }

        Optional.ofNullable(sinks.get(sms.getSender()))
            .ifPresentOrElse(
                sink -> {
                    try{
                        sink.tryEmitNext(sms);
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
        for(Many<SMS> sink: sinks.values()){
            sink.tryEmitComplete();
        }
        sinks.clear();
    }

}
