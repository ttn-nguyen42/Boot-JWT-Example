package com.example.authentication_example.config;

import com.example.authentication_example.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.security.jwt.keystore-location}")
    private String keystorePath;

    @Value("${app.security.jwt.keystore-password}")
    private String keystorePassword;

    @Value("${app.security.jwt.key-alias}")
    private String keyAlias;

    @Value("${app.security.jwt.private-key-passphrase}")
    private String privateKeyPassphrase;

    @Bean
    public KeyStore keyStore() {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream resourceAsStream = Thread.currentThread()
                                                 .getContextClassLoader()
                                                 .getResourceAsStream(keystorePath);
            ks.load(resourceAsStream, keystorePassword.toCharArray());
            return ks;
        } catch (Exception e) {
            log.error("Unable to load keystore: {}", keystorePath, e);
        }
        throw new IllegalArgumentException("Unable to load keystore");
    }

    @Bean
    public RSAPrivateKey jwtSigningKey(KeyStore ks) {
        try {
            Key key = ks.getKey(keyAlias, privateKeyPassphrase.toCharArray());
            if (key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
        } catch (Exception e) {
            log.error("Unable to load private key from keystore: {}", keystorePath, e);
        }
        throw new IllegalArgumentException("Unable to load private key");
    }

    @Bean
    public RSAPublicKey jwtValidationKey(KeyStore ks) {
        try {
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey key = cert.getPublicKey();
            if (key instanceof RSAPublicKey) {
                return (RSAPublicKey) key;
            }
        } catch (Exception e) {
            log.error("Unable to load public key from keystore: {}", keystorePath, e);
        }
        throw new IllegalArgumentException("Unable to load public key");
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey key) {
        return NimbusJwtDecoder.withPublicKey(key).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cf = new CorsConfiguration();
        cf.setAllowedOrigins(List.of("*"));
        cf.setAllowedMethods(List.of("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
        cf.addAllowedOrigin("*");
        cf.addAllowedHeader("*");
        cf.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cf);
        return src;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
            .disable()
            .formLogin().disable().csrf().ignoringAntMatchers("/api/v1/**").and().cors()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, Constants.REGISTER_PATH)
            .permitAll()
            .antMatchers(HttpMethod.POST, Constants.LOGIN_PATH)
            .permitAll()
            .antMatchers(HttpMethod.POST, Constants.TOKEN_PATH)
            .permitAll()
            .antMatchers(HttpMethod.DELETE, Constants.LOGOUT_PATH)
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.jwtAuthenticationConverter(getJwtAuthenticationConverter())))
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix(Constants.AUTHORITY_PREFIX);
        authoritiesConverter.setAuthoritiesClaimName(Constants.ROLE_CLAIM);
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
