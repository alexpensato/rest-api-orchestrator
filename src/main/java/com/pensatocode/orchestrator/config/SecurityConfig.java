package com.pensatocode.orchestrator.config;

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

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/", "/admin/login", "/admin/login/**").permitAll()
                        .pathMatchers("/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/**").hasRole("SERVER")
                        .anyExchange().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/admin/login")
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            webFilterExchange.getExchange().getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
