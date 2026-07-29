package app.news.backend.service;

import app.news.backend.dto.request.RefreshTokenRequest;
import app.news.backend.model.RefreshToken;
import app.news.backend.repository.RefreshTokenRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  public RefreshToken refresh(RefreshTokenRequest request) {
    Optional<RefreshToken> opt_token = refreshTokenRepository.findByToken(request.refreshToken());

    RefreshToken token =
        opt_token.orElseThrow(() -> new IllegalArgumentException("Invalid Expression"));

    return token;
  }
}
