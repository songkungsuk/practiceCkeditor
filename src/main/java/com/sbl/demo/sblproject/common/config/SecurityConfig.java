package com.sbl.demo.sblproject.common.config;

import com.sbl.demo.sblproject.common.config.filter.JwtAuthenticationFilter;
import com.sbl.demo.sblproject.common.config.security.JwtAccessDeniedHandler;
import com.sbl.demo.sblproject.common.config.security.JwtAuthenticationEntryPoint;
import com.sbl.demo.sblproject.common.config.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] DEFAULT_LIST = {
        "/",
        "/index",
        "/login"
    };
    private static final String[] WHITE_LIST = {
        "/main",
        "/register",
        "/auth",
        "/dashBoard",
        "/management",
        "/permission",
        "/masterData",
        "/editor",
        "/user-detail/*",
        "/assets/**",
        "/vendor/**",
        "/upload/**",
        "/static/**",
    };
    private static final String[] USER_API_WHITE_LIST = {
        "/api/v1/login",
        "/api/v1/user/check-userId",
        "/api/v1/user/register"
    };
    private static final String[] API_V1 = {
        "/api/v1/**"
    };
    private static final String[] API_V2 = {
        "/api/v2/**"
    };
    private static final String[] API_V3 = {
        "/api/v3/**"
    };

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()
                    .requestMatchers(DEFAULT_LIST).permitAll()
                    .requestMatchers(WHITE_LIST).permitAll()
                    .requestMatchers(USER_API_WHITE_LIST).permitAll()
                    .requestMatchers(API_V1).hasAnyRole("USER", "MANAGER", "ADMIN")
                    .requestMatchers(API_V2).hasAnyRole("MANAGER", "ADMIN")
                    .requestMatchers(API_V3).hasRole("ADMIN")
                    .anyRequest().authenticated();
            })
            .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler))
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
