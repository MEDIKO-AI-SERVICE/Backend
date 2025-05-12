package com.mediko.mediko_server.global.jasypt.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class JasyptConfig {

    @Value("${JASYPT_ENCRYPT_KEY}") // 환경 변수에서 주입됨
    private String key;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(key);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256"); // AES-256 알고리즘
        config.setPoolSize("1");
        config.setStringOutputType("base64");
        config.setKeyObtentionIterations("1000");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator"); // AES 필수

        encryptor.setConfig(config);
        return encryptor;
    }
}
