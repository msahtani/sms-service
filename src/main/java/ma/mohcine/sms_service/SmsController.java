package ma.mohcine.sms_service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/app-gw")
@RequiredArgsConstructor
@Log4j2
public class SmsController {
    

    private final SseService sseService;

    
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SMS> register(
        @RequestParam(name="id") String phoneNumber,
        ServerWebExchange exchage
    ) throws IOException{

        
        Flux<SMS> emitter = sseService.register(phoneNumber);

        //exchage.getRequest().getRemoteAddress().
        
        log.info(
            "received a SSE connection from {}:{} for {}",
            exchage.getRequest().getRemoteAddress().getHostName(),
            exchage.getRequest().getRemoteAddress().getPort(),
            phoneNumber
        );

        return emitter;
    
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/send")
    public Mono<Void> send(@RequestBody SMS sms) throws IOException {
        return sseService.send(sms);
    }

}