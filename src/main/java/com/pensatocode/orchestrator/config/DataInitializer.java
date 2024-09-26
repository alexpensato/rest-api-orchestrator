package com.pensatocode.orchestrator.config;

import com.pensatocode.orchestrator.model.User;
import com.pensatocode.orchestrator.service.TotpService;
import com.pensatocode.orchestrator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TotpService totpService;

    @Override
    public void run(String... args) throws Exception {
        createAdminUserIfNotExists()
            .subscribe(
                result -> System.out.println("****** " + result),
                error -> System.err.println("Error in admin user initialization: " + error.getMessage())
            );
    }

    private Mono<String> createAdminUserIfNotExists() {
        return userService.findByUsername("admin")
            .flatMap(existingUser -> Mono.just("Admin user already exists. Skipping initialization."))
            .switchIfEmpty(createAdminUser());
    }

    /**
     * Example of OTP URL:
     * otpauth://totp/API%20Orchestrator:admin?secret=ABCDE&issuer=API%20Orchestrator
     */
    private Mono<String> createAdminUser() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("Password!1");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");

        return userService.createUser(adminUser)
            .flatMap(createdUser -> totpService.generateSecretKey(createdUser.getUsername()))
            .map(secretKey -> "Admin user created with TOTP secret: " + secretKey);
    }
}
