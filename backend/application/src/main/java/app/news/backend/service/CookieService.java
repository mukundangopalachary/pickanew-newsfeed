package app.news.backend.service;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;

public class CookieService {
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
