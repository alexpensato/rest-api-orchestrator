package com.pensatocode.orchestrator.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerAuthenticationProvider implements ReactiveAuthenticationManager {

    private final Map<String, String> serverCredentials = new ConcurrentHashMap<>();

    public ServerAuthenticationProvider() {
        // Initialize with some server credentials
        serverCredentials.put("server1", "password1");
        serverCredentials.put("server2", "password2");
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> auth.getName() != null && auth.getCredentials() != null)
                .flatMap(auth -> {
                    String name = auth.getName();
                    String password = auth.getCredentials().toString();

                    if (serverCredentials.containsKey(name) && serverCredentials.get(name).equals(password)) {
                        return Mono.just(new UsernamePasswordAuthenticationToken(
                                name, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_SERVER"))));
                    } else {
                        return Mono.empty();
                    }
                });
    }
}
