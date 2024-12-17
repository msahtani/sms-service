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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/app-gw")
@RequiredArgsConstructor
@Log4j2
public class SmsController {
    

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter register(
        @RequestParam(name="phoneNumber", required=false) 
        String phoneNumber,
        HttpServletRequest request
    ) throws IOException{
        SseEmitter emitter = sseService.register(phoneNumber);
        
        log.info(
            "received a SSE connection from {}:{}",
            request.getRemoteAddr(), 
            request.getRemotePort()
        );

        return emitter;
    
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/send")
    public void send(@RequestBody SMS sms) throws IOException {
        sseService.send(sms);
    }

}
