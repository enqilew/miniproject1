package miniproject1.cryptocurr.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import miniproject1.cryptocurr.services.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    // Bean for password encoding
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure the security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF for development purposes (not recommended for production)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/api/register").permitAll() // Publicly accessible endpoints
                .requestMatchers("/api/user/favourites").permitAll()
                .requestMatchers("/images/**", "/css/**", "/js/**").permitAll() // Allow access to static resources
                .anyRequest().authenticated() // All other requests need authentication
            )
            .formLogin(form -> form
                .loginPage("/login") // Custom login page
                .defaultSuccessUrl("/dashboard", true) // Redirect to dashboard after successful login
                .failureUrl("/login?error=true") // Redirect to login page with error on failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL to trigger logout
                .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                .permitAll()
            );
            

        return http.build();
    }

    // Bean for authentication manager with custom user details service
    @Bean
    public AuthenticationManager authManager(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

}


