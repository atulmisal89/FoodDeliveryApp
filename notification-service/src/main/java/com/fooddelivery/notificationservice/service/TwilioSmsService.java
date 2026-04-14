package com.fooddelivery.notificationservice.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TwilioSmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty() && !accountSid.contains("your-")) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio credentials not configured. SMS will be logged but not sent.");
        }
    }

    public void sendSms(String toPhoneNumber, String message) {
        try {
            if (accountSid == null || accountSid.isEmpty() || accountSid.contains("your-")) {
                log.info("[SMS MOCK] To: {}, Message: {}", toPhoneNumber, message);
                return;
            }

            // Format phone number (add +91 for India if not present)
            String formattedNumber = formatPhoneNumber(toPhoneNumber);

            Message twilioMessage = Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message
            ).create();

            log.info("SMS sent successfully. SID: {}", twilioMessage.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Remove all non-digit characters
        String digits = phoneNumber.replaceAll("\\D", "");
        
        // If starts with 0, remove it
        if (digits.startsWith("0")) {
            digits = digits.substring(1);
        }
        
        // If doesn't start with country code (91 for India), add it
        if (!digits.startsWith("91") && digits.length() == 10) {
            digits = "91" + digits;
        }
        
        return "+" + digits;
    }
}
