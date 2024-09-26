package com.pensatocode.orchestrator.controller;

import com.pensatocode.orchestrator.service.ApiRegistrationService;
import com.pensatocode.orchestrator.service.FilterScriptService;
import com.pensatocode.orchestrator.service.ScriptExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {

    @Autowired
    private ApiRegistrationService apiRegistrationService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ScriptExecutionService scriptExecutionService;

    @Autowired
    private FilterScriptService filterScriptService;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @PreAuthorize("hasRole('SERVER')")
    public Mono<ResponseEntity<String>> routeRequest(@RequestBody(required = false) String body,
                                                     @RequestParam Map<String, String> queryParams,
                                                     @RequestHeader Map<String, String> headers,
                                                     @PathVariable String[] path) {
        String apiName = path[0];
        String remainingPath = String.join("/", java.util.Arrays.copyOfRange(path, 1, path.length));

        return apiRegistrationService.getApiRegistrationByName(apiName)
                .flatMap(apiRegistration -> 
                    filterScriptService.getScript(apiName)
                        .defaultIfEmpty("")  // If no script is found, use an empty string
                        .flatMap(filterScript -> {
                            Map<String, Object> requestData = Map.of(
                                    "body", body,
                                    "queryParams", queryParams,
                                    "headers", headers,
                                    "path", remainingPath
                            );

                            return executeFilterScript(filterScript, requestData)
                                    .flatMap(allowed -> {
                                        if (!allowed) {
                                            return Mono.just(ResponseEntity.status(403).body("Request not allowed by filter script"));
                                        }
                                        
                                        WebClient client = webClientBuilder.baseUrl(apiRegistration.getBaseUrl()).build();
                                        
                                        return client.method(HttpMethod.valueOf(headers.getOrDefault("X-HTTP-Method-Override", "GET")))
                                                .uri(uriBuilder -> {
                                                    uriBuilder.path(remainingPath);
                                                    queryParams.forEach(uriBuilder::queryParam);
                                                    return uriBuilder.build();
                                                })
                                                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                                                .bodyValue(body != null ? body : "")
                                                .exchange()
                                                .flatMap(clientResponse -> clientResponse.toEntity(String.class));
                                    });
                        })
                )
                .onErrorResume(this::handleError);
    }

    private Mono<Boolean> executeFilterScript(String script, Map<String, Object> requestData) {
        if (script.isEmpty()) {
            return Mono.just(true);
        }
        return scriptExecutionService.executeFilterScript(script, requestData);
    }

    private Mono<ResponseEntity<String>> handleError(Throwable throwable) {
        // Log the error
        return Mono.just(ResponseEntity.status(503).body("Service Unavailable: " + throwable.getMessage()));
    }
}
