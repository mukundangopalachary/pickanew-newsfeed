package app.news.backend.dto.response;

import java.time.OffsetDateTime;

public record ErrorResponse(
    int status,
    String error,
    String message,
    OffsetDateTime timestamp) {
}
