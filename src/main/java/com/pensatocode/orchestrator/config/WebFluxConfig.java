package com.pensatocode.orchestrator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    private final ThymeleafReactiveViewResolver thymeleafReactiveViewResolver;

    public WebFluxConfig(ThymeleafReactiveViewResolver thymeleafReactiveViewResolver) {
        this.thymeleafReactiveViewResolver = thymeleafReactiveViewResolver;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafReactiveViewResolver);
    }
}
