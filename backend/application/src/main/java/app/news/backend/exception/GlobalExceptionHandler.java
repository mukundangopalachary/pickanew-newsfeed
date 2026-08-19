package app.news.backend.exception;

import app.news.backend.dto.response.ErrorResponse;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(409, "UserAlreadyExistsError", ex.getMessage(), Instant.now()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex){
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, "BadCredentialsError", ex.getMessage(), Instant.now()));
  }

  @ExceptionHandler(TokenNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundRefreshToken(TokenNotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(404, "TokenNotFoundException", ex.getMessage(), Instant.now()));
  }
}
