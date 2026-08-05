package app.news.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String name, @NotBlank String email, @NotBlank String password) {
}
