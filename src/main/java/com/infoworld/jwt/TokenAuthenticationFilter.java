package com.infoworld.jwt;

//import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static java.util.Optional.ofNullable;
//import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.removeStart;

//@FieldDefaults(level = PRIVATE, makeFinal = true)
final class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
  TokenAuthenticationFilter(final RequestMatcher requiresAuth) { super(requiresAuth); }

  /**
   * Called when a secured resource is requested.
   */
  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
                                              final HttpServletResponse response) {
    final String param = ofNullable(request.getHeader(AUTHORIZATION)).orElse(request.getParameter("t"));

    final String token = ofNullable(param).map(value -> removeStart(value, "Bearer"))
      .map(String::trim).orElseThrow(() -> new BadCredentialsException("No Token Found!"));

    final Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
    System.out.println("^^^^^^^^ auth: " + auth);
    return getAuthenticationManager().authenticate(auth);
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest request, 
        final HttpServletResponse response, final FilterChain chain,
        final Authentication authResult) throws IOException, ServletException {
      super.successfulAuthentication(request, response, chain, authResult);
      chain.doFilter(request, response);
  }
}
