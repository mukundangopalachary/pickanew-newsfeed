package app.news.backend.exception;

public class TokenExpiredException extends RuntimeException {

  public TokenExpiredException(String message) {
    super(message);
  }

  public TokenExpiredException(String message, String token) {
    super(String.format("[%s] -> %s", token, message));
  }
}
