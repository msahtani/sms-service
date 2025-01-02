package ma.mohcine.sms_service;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SMS {
    private String sender;
    private String phoneNumber;
    private String message;
}
