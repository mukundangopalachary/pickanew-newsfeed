package app.news.backend.repository;

import app.news.backend.model.Topic;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
  boolean existsByName(String name);
  boolean existsById(Long id);
  Topic findTopicById(Long id);
  Topic findTopicByName(String name);
  OffsetDateTime findCreatedAtById(Long id);
}
