package app.news.backend.controller;

import app.news.backend.service.AuthService;
import app.news.backend.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

  @Autowired RefreshTokenService refreshTokenService;

  @Autowired AuthService authService;

  private void setJwtCookie(HttpServletResponse response, String token) {

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

  private void setRefreshTokenCookie(HttpServletResponse response, String token) {
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
}
