package app.news.backend.dto.response;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType, // always "Bearer"
    Long expiresIn, // ms
    UserResponse user) {
}
