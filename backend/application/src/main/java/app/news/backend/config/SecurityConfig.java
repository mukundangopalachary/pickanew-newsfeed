package app.news.backend.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

  @Autowired
  private JwtFilter jwtFilter;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        (authorize) -> authorize
            .requestMatchers("api/v1/register", "api/v1/login")
            .permitAll()
            .anyRequest().authenticated())
        .csrf((csrf) -> csrf.disable())
        .httpBasic(Customizer.withDefaults())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .formLogin(Customizer.withDefaults())
        .logout((logout) -> logout.logoutSuccessUrl("/"));

    return http.build();
  }

}
/*
 * Caused by: org.springframework.beans.factory.BeanCreationException: Error
 * creating bean with name 'securityFilterChain' defined in class path resource
 * [app/news/backend/config/SecurityConfig.class]: Failed to instantiate
 * [org.springframework.security.web.SecurityFilterChain]: Factory method
 * 'securityFilterChain' threw exception with message: pattern must start with a
 * /
 * at org.springframework.beans.factory.support.ConstructorResolver.instantiate(
 * ConstructorResolver.java:657)
 * at org.springframework.beans.factory.support.ConstructorResolver.
 * instantiateUsingFactoryMethod(ConstructorResolver.java:645)
 * at
 * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.
 * instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1360)
 * at
 * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.
 * createBeanInstance(AbstractAutowireCapableBeanFactory.java:1192)
 */
