package app.news.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.news.backend.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
