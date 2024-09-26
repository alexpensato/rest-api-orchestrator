package com.pensatocode.orchestrator.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import org.apache.commons.codec.binary.Base32;

@Component
public class EncryptionUtil {

    @Value("${encryption.secret.key}")
    private String secretKey;

    private final Base32 base32 = new Base32();

    public static class EncryptionResult {
        public final String encryptedData;
        public final String iv;

        public EncryptionResult(String encryptedData, String iv) {
            this.encryptedData = encryptedData;
            this.iv = iv;
        }
    }

    public EncryptionResult encrypt(String value) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(base32.decode(value));

        return new EncryptionResult(
                base32.encodeAsString(encrypted),
                Base64.getEncoder().encodeToString(iv)
        );
    }

    public String decrypt(String encrypted, String ivString) throws Exception {
        byte[] iv = Base64.getDecoder().decode(ivString);
        byte[] encryptedBytes = base32.decode(encrypted);

        SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return base32.encodeAsString(decrypted);
    }
}