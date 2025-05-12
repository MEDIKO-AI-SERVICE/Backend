package com.mediko.mediko_server.global.jasypt;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JasyptEnvBasedEncryptor implements CommandLineRunner {

    @Value("${JASYPT_ENCRYPT_KEY}")
    private String key;

    @Override
    public void run(String... args) {
        String[] plainTexts = {};

        PooledPBEStringEncryptor encryptor = createEncryptor(key);

        for (String plainText : plainTexts) {
            String encrypted = encryptor.encrypt(plainText);
            System.out.printf("원본: %-40s → ENC(%s)%n", plainText, encrypted);
        }
    }

    private PooledPBEStringEncryptor createEncryptor(String key) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(key);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setStringOutputType("base64");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");

        encryptor.setConfig(config);
        return encryptor;
    }
}
