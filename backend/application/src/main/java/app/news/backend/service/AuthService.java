package app.news.backend.service;

import app.news.backend.dto.request.LoginRequest;
import app.news.backend.dto.request.RegisterRequest;
import app.news.backend.model.User;
import app.news.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Autowired UserRepository userRepository;

  @Autowired private AuthenticationManager authManager;

  //SAVE NEW USER
  public User save(RegisterRequest request) {
    String name = request.name();
    String password = encoder.encode(request.password());
    String email = request.email();

    User user = new User();
    user.setUsername(name);
    user.setPasswordHash(password);
    user.setEmail(email);

    return userRepository.save(user);
  }

  //VERIFY USER LOGIN
  public Authentication verify(LoginRequest user) throws BadCredentialsException{
    try {
      Authentication authentication =
          authManager.authenticate(
              new UsernamePasswordAuthenticationToken(user.name(), user.password()));
      return authentication;
    } catch (BadCredentialsException e) {
      throw e;
    }
  }

  public void setRefreshTokenCookie(HttpServletResponse response, String token) {
    ResponseCookie cookie =
      ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/v1/auth/refresh")
            .maxAge(Duration.ofDays(30))
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  

public void setJwtCookie(HttpServletResponse response, String token) {

    ResponseCookie cookie =
        ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .secure(true) // not necessary for development
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ofDays(1))
            .build();

    System.out.println(cookie.toString());

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
