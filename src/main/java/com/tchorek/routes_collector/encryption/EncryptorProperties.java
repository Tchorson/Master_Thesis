package com.tchorek.routes_collector.encryption;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class EncryptorProperties {

    @Value("${AES_KEY}")
    private String key;

    @Value("${INIT_VECTOR}")
    private String iv;
}
