package com.pensatocode.orchestrator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table("totp_secrets")
public class TotpSecret {
    @Id
    private Long id;
    private String username;
    private String secretKey;
    private String iv;
}
