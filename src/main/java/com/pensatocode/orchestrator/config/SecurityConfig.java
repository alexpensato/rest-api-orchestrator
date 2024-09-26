package com.pensatocode.orchestrator.config;

import com.pensatocode.orchestrator.security.ServerAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Autowired
    private ServerAuthenticationProvider serverAuthenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/admin/**").hasRole("ADMIN")
                    .pathMatchers("/api/**").hasRole("SERVER")
                    .anyExchange().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .formLogin(formLogin -> formLogin
                    .loginPage("/login")
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                )
                .authenticationManager(serverAuthenticationProvider)
                .build();
    }
}
