package com.pensatocode.orchestrator.repository;

import com.pensatocode.orchestrator.model.ApiRegistration;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApiRegistrationRepository extends R2dbcRepository<ApiRegistration, Long> {
    Mono<ApiRegistration> findByName(String name);
}
