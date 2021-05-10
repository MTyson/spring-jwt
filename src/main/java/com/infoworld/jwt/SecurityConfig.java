package com.infoworld.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static java.util.Objects.requireNonNull;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
//@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
    new AntPathRequestMatcher("/"), new AntPathRequestMatcher("/public/**")
  );

  private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);

  @Override
  public void configure(final WebSecurity web) {
    web.ignoring().requestMatchers(PUBLIC_URLS);
  }
  TokenAuthenticationProvider provider;

  SecurityConfig(final TokenAuthenticationProvider provider) {
    super();
    this.provider = requireNonNull(provider);
  }
  protected void configure(final HttpSecurity http) throws Exception {
    http
      .sessionManagement() 
      .sessionCreationPolicy(STATELESS)
      .and()
      .exceptionHandling()
      .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
      .and()
      .authenticationProvider(provider)
      .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
      .authorizeRequests()
      .requestMatchers(PROTECTED_URLS)
      .authenticated()
      .and()
      .csrf().disable()
      .formLogin().disable()
      .httpBasic().disable()
      .logout().disable();
  }
  @Bean
  AuthenticationEntryPoint forbiddenEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
  }
  @Bean
  SimpleUrlAuthenticationSuccessHandler successHandler() {
    final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
    successHandler.setRedirectStrategy(new NoRedirectStrategy());
    return successHandler;
  }
  @Bean
  TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
    final TokenAuthenticationFilter filter = new TokenAuthenticationFilter(PROTECTED_URLS);
    filter.setAuthenticationManager(authenticationManager());
    filter.setAuthenticationSuccessHandler(successHandler());
    return filter; 
  }
  /*@Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
      .sessionManagement()
      .sessionCreationPolicy(STATELESS)
      .and()
      .exceptionHandling()
      // this entry point handles when you request a protected page and you are not yet
      // authenticated
      .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
      .and()
      .authenticationProvider(provider)
      .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
      .authorizeRequests()
      .requestMatchers(PROTECTED_URLS)
      .authenticated()
      .and()
      .csrf().disable()
      .formLogin().disable()
      .httpBasic().disable()
      .logout().disable();
  }
  */
}
