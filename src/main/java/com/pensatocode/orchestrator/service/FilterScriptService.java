package com.pensatocode.orchestrator.service;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class FilterScriptService {

    private final AsyncCache<String, String> cache;

    public FilterScriptService() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(100)
                .buildAsync();
    }

    public Mono<Void> saveScript(String endpoint, String script) {
        cache.put(endpoint, CompletableFuture.completedFuture(script));
        return Mono.empty();
    }

    public Mono<String> getScript(String endpoint) {
        return Mono.fromFuture(cache.get(endpoint, key -> null))
                .filter(script -> script != null);
    }

    public Mono<Void> deleteScript(String endpoint) {
        return Mono.fromRunnable(() -> cache.synchronous().invalidate(endpoint));
    }
}