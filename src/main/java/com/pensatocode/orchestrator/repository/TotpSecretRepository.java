package com.pensatocode.orchestrator.repository;

import com.pensatocode.orchestrator.model.TotpSecret;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TotpSecretRepository extends R2dbcRepository<TotpSecret, Long> {
    Mono<TotpSecret> findByUsername(String username);
}
