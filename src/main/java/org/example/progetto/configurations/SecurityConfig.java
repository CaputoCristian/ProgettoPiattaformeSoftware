package org.example.progetto.configurations;

import org.example.progetto.jwt.CustomJwt;
import org.example.progetto.jwt.CustomJwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // Permette @PreAuthorize
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/cart/**").authenticated()
//                                .requestMatchers("/products/**").authenticated()
                                .requestMatchers("/purchases/**").authenticated()
                                .requestMatchers("/sales/**").authenticated()
                                .anyRequest().permitAll()
//                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(CustomjwtConverter())));
        return http.build();
    }

    @Bean
    public Converter<Jwt, CustomJwt> CustomjwtConverter() {
        return new CustomJwtConverter();
    }
}