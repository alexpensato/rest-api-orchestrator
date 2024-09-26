package com.pensatocode.orchestrator.service;

import com.pensatocode.orchestrator.model.TotpSecret;
import com.pensatocode.orchestrator.repository.TotpSecretRepository;
import com.pensatocode.orchestrator.util.EncryptionUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TotpService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Autowired
    private TotpSecretRepository totpSecretRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    public Mono<String> generateSecretKey(String username) {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();  // This is already in Base32 format

        return Mono.fromCallable(() -> encryptionUtil.encrypt(secretKey))
                .flatMap(encryptionResult -> {
                    TotpSecret totpSecret = new TotpSecret();
                    totpSecret.setUsername(username);
                    totpSecret.setSecretKey(encryptionResult.encryptedData);
                    totpSecret.setIv(encryptionResult.iv);
                    return totpSecretRepository.save(totpSecret);
                })
                .thenReturn(secretKey);  // Return the unencrypted secret key for immediate use
    }

    public Mono<Boolean> verifyCode(String username, String code) {
        return totpSecretRepository.findByUsername(username)
                .flatMap(totpSecret -> Mono.fromCallable(() ->
                        encryptionUtil.decrypt(totpSecret.getSecretKey(), totpSecret.getIv())
                ))
                .map(secretKey -> gAuth.authorize(secretKey, Integer.parseInt(code)))
                .defaultIfEmpty(false);
    }

    public Mono<String> getQrCodeUrl(String username) {
        return totpSecretRepository.findByUsername(username)
                .switchIfEmpty(generateSecretKey(username).flatMap(secretKey -> totpSecretRepository.findByUsername(username)))
                .flatMap(totpSecret -> Mono.fromCallable(() ->
                        encryptionUtil.decrypt(totpSecret.getSecretKey(), totpSecret.getIv())
                ))
                .map(secretKey ->
                        GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("API Orchestrator", username, new GoogleAuthenticatorKey.Builder(secretKey).build())
                );
    }
}