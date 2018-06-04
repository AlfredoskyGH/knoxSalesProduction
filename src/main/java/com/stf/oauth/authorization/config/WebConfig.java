package com.stf.oauth.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.web.session.HttpSessionEventPublisher;


/**
 * Configuración del servidor de seguridad y recursos
 * <p>
 * La anotación @EnableResourceServer configura el mecanismo de OAuth 2.0 para los recursos
 */
@Configuration
@EnableResourceServer
public class WebConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().loginPage("/login")
                .usernameParameter("username").passwordParameter("password").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/logout/**",
                        "/oauth/authorize", "/oauth/token",
                        "/oauth/authorize**", "/oauth/token**",
                        "/oauth/confirm_access", "/user-info", "/permissions", "/sso/logout-sso", "/password/**")
                .permitAll()
                .antMatchers("/sso/", "/sso/**").hasRole("ADMIN_SSO").filterSecurityInterceptorOncePerRequest(true)
                .anyRequest()
                .authenticated();
    }
}

