package app.news.backend.repository;

import app.news.backend.model.ArticleLikes;
import app.news.backend.model.ArticleUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleLikesRepository extends JpaRepository<ArticleLikes, ArticleUserId> {
}
