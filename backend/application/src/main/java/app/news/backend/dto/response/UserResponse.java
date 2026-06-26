package app.news.backend.dto.response;

public record UserResponse(
    Long id,
    String name,
    String email,
    String role,
    String provider) {
}
