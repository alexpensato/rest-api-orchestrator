package com.pensatocode.orchestrator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table("api_registrations")
public class ApiRegistration {
    @Id
    private Long id;
    private String name;
    private String baseUrl;
    private String swaggerUrl;
}
