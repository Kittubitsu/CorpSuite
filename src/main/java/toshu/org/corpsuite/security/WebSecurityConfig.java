package toshu.org.corpsuite.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.requestMatchers("/", "/error", "/css/**", "/js/**", "/images/**")
                        .permitAll().anyRequest().authenticated())
                .formLogin((form) -> form.loginPage("/").permitAll())
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }
}
