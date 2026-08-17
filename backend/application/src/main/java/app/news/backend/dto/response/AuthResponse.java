package app.news.backend.dto.response;

public record AuthResponse(
    UserResponse user, 
    String message
){}
