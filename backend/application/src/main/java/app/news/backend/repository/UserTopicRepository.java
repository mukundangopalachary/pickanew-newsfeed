package app.news.backend.repository;

import app.news.backend.model.UserTopic;
import app.news.backend.model.UserTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTopicRepository extends JpaRepository<UserTopic, UserTopicId> {
}
