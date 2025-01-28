package ma.mohcine.sms_service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks.Many;
import static reactor.core.publisher.Sinks.many;


@Service
@RequiredArgsConstructor
@Log4j2
public class SseService {

    private final StringRedisTemplate redisTemplate;
    
    @Value("${custom.internal-ip}")
    private String internalIp;
    
    private Map<String, Many<SMS>> sinks = new HashMap<>();


    public Flux<SMS> register(String phoneNumber) throws IOException {
        
        
        String key = Optional
            .ofNullable(phoneNumber)
            .orElse(UUID.randomUUID().toString());

        // register in the redis
        redisTemplate.opsForValue().set(key, internalIp);
        //
        

        Many<SMS> sink = many().multicast().onBackpressureBuffer();

    
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

    
    private void redirectToServer(String ip, SMS sms){
        log.info("redirecting to the server {}", ip);
        sms.setRedirectedFrom(internalIp);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.postForEntity(
            "http://%s/app-gw/send".formatted(ip),
            sms, Object.class
        );

    }


    public Mono<Void> send(SMS sms) throws IOException {

        final String sender = sms.getSender();
        final Many<SMS> sink = sinks.get(sender);

        if(sink == null){
            String ip = redisTemplate.opsForValue().get(sender);
            if(ip != null) {
                redirectToServer(ip, sms);
                
            }else{
                throw new RuntimeException("the device is not connected");
            }
        }else {
            var result = sink.tryEmitNext(sms);
            log.debug("result status: ", result);
        }

    
        return Mono.empty();
        
    }

    
    public void cleanUp(){
        for(Many<SMS> sink: sinks.values()){
            sink.tryEmitComplete();
        }
        sinks.clear();
    }

}
