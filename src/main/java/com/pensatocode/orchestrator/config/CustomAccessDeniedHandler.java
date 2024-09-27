package com.pensatocode.orchestrator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> logAccessDenied(exchange, denied, securityContext.getAuthentication()))
                .then(Mono.fromRunnable(() -> {
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                }));
    }

    private Mono<Void> logAccessDenied(ServerWebExchange exchange, AccessDeniedException denied, Authentication auth) {
        logger.error("Access Denied Error: {}", denied.getMessage());
        logger.error("Denied URL: {}", exchange.getRequest().getPath().value());
        logger.error("User: {}", auth != null ? auth.getName() : "Unknown");
        logger.error("Authorities: {}", auth != null ? auth.getAuthorities() : "None");
        return Mono.empty();
    }
}
