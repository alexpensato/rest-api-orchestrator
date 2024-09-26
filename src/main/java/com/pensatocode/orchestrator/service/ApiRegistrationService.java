package com.pensatocode.orchestrator.service;

import com.pensatocode.orchestrator.model.ApiRegistration;
import com.pensatocode.orchestrator.repository.ApiRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApiRegistrationService {

    @Autowired
    private ApiRegistrationRepository apiRegistrationRepository;

    public Flux<ApiRegistration> getAllApiRegistrations() {
        return apiRegistrationRepository.findAll();
    }

    public Mono<ApiRegistration> getApiRegistrationById(Long id) {
        return apiRegistrationRepository.findById(id);
    }

    public Mono<ApiRegistration> getApiRegistrationByName(String name) {
        return apiRegistrationRepository.findByName(name);
    }

    public Mono<ApiRegistration> createApiRegistration(ApiRegistration apiRegistration) {
        return apiRegistrationRepository.save(apiRegistration);
    }

    public Mono<ApiRegistration> updateApiRegistration(Long id, ApiRegistration apiRegistration) {
        return apiRegistrationRepository.findById(id)
                .flatMap(existingRegistration -> {
                    existingRegistration.setName(apiRegistration.getName());
                    existingRegistration.setBaseUrl(apiRegistration.getBaseUrl());
                    existingRegistration.setSwaggerUrl(apiRegistration.getSwaggerUrl());
                    return apiRegistrationRepository.save(existingRegistration);
                });
    }

    public Mono<Void> deleteApiRegistration(Long id) {
        return apiRegistrationRepository.deleteById(id);
    }

    public Mono<Long> getApiCount() {
        return apiRegistrationRepository.count();
    }
}
