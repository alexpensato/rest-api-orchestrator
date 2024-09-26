package com.pensatocode.orchestrator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table("users")
public class User {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
}
