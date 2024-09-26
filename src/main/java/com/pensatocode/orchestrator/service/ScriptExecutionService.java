package com.pensatocode.orchestrator.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.util.Map;

@Service
public class ScriptExecutionService {

    private final ScriptEngine engine;

    public ScriptExecutionService() {
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    public Mono<Boolean> executeFilterScript(String script, Map<String, Object> parameters) {
        return Mono.fromCallable(() -> {
            SimpleBindings bindings = new SimpleBindings();
            bindings.putAll(parameters);

            Object result = engine.eval(script, bindings);
            return result instanceof Boolean ? (Boolean) result : false;
        }).onErrorResume(e -> {
            // Log the error
            return Mono.just(false);
        });
    }
}
