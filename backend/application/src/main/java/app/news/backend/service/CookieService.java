package app.news.backend.service;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieService {
  public void setRefreshTokenCookie(HttpServletResponse response, String token) {
    ResponseCookie cookie =
      ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/v1/auth/")
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


    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  public void clearJwtCookie(HttpServletResponse response){
    ResponseCookie cookie = ResponseCookie.from("jwt", "")
      .httpOnly(true)
      .secure(true)
      .sameSite("Lax")
      .path("/")
      .maxAge(Duration.ZERO)
      .build();

    response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
  }

  public void clearRefreshTokenCookie(HttpServletResponse response){
    ResponseCookie cookie =
      ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/v1/auth/")
            .maxAge(Duration.ZERO)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
