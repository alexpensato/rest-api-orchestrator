package com.pensatocode.orchestrator.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TotpUrlGenerator {

    public static String generateTotpUrl(String issuer, String username, String secretKey) {
        String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                encodedIssuer, encodedUsername, secretKey, encodedIssuer);
    }
}
