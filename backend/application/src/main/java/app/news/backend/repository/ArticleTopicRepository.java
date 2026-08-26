package app.news.backend.repository;

import app.news.backend.model.ArticleTopic;
import app.news.backend.model.ArticleTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleTopicRepository extends JpaRepository<ArticleTopic, ArticleTopicId> {
}
