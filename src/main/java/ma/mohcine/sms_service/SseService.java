package ma.mohcine.sms_service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SseService {

    private final Map<String, SseEmitter> sses;

    public SseService() {
        this.sses = new HashMap<>();
    }

    private SseEmitter createSSE(
            final String address,
            final int port
    ) {

        final String key = "%s:%d".formatted(address, port);

        final SseEmitter emitter = new SseEmitter(0L);
        sses.put(key, emitter);


        emitter.onError(ex -> {
            log.error("error: {}", ex.getMessage());
            emitter.complete();
        });

        emitter.onCompletion(() -> {
            log.info(
                " {}:{} : SSE connection is completed from the server",
                address, port 
            );
            sses.remove(key);
        });

        return emitter;
    }

    public SseEmitter register(HttpServletRequest request) {
        
        final var address = request.getRemoteAddr();
        final var port = request.getRemotePort();

        var emitter = createSSE(address, port);

        
        try{
            emitter.send("");
        }catch(Exception ignored){
            throw new RuntimeException();
        }
        

        log.info(
                "received a SSE connection from {}:{}",
                address, port
        );

        return emitter;
    }

    public void send(SMS sms) throws IOException {

        // send to the all (actually it's just one connected device in the map)
        for(SseEmitter sse: sses.values()){
            sse.send(sms);
        }

    }


}
