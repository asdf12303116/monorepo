package xyz.chen.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import xyz.chen.commons.base.FilterExceptionHandler;
import xyz.chen.commons.config.ConfigData;
import xyz.chen.member.filter.JwtAuthenticationFilter;
import xyz.chen.member.services.AuthService;
import xyz.chen.member.utils.exceptionUtils;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    ConfigData configData;


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailService() {
        return new AuthService();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public FilterExceptionHandler filterExceptionHandler() {
        return new FilterExceptionHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/actuator", "/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                .requestMatchers(configData.getPermitAllUrls().toArray(String[]::new)).permitAll()
                .anyRequest().authenticated()
        );
        http.oauth2Login(Customizer.withDefaults());
        http.exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint()));
        http.addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(filterExceptionHandler(), SecurityContextHolderFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return ((request, response, authException) -> exceptionUtils.genExceptionResp(response, authException));
    }


    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return ((request, response, exception) -> exceptionUtils.genExceptionResp(response, exception));
    }
}
