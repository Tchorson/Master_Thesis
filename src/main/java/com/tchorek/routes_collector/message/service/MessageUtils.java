package com.tchorek.routes_collector.message.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MessageUtils {

    @Value("${SOURCE_EMAIL}")
    private String fromEmail;

    @Value("${SOURCE_EMAIL_PASS}")
    private String password;

    @Value("${TARGET_EMAIL}")
    private String toEmail;
}
