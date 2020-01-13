package testApplication.configuration.security

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@EnableWebFluxSecurity
class SecurityConfig {
        @Bean
        fun springSecurityFilterChain(
            http: ServerHttpSecurity
        ): SecurityWebFilterChain {
            return http.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()

                .build()
        }
    }