package app.news.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import app.news.backend.model.Topic;
import app.news.backend.model.User;
import app.news.backend.model.UserTopic;
import app.news.backend.model.UserTopicId;
import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface UserTopicRepository
        extends JpaRepository<UserTopic, UserTopicId> {

    List<UserTopic> findByUser(User user);
    
    @Query(""" 
      SELECT ut FROM UserTopic ut WHERE ut.topic.id = :topicId AND ut.subscribed = true 
    """)
    List<UserTopic> findSubscribedUsersByTopicId(@Param("topicId") Long topicId);

    @Query("""
    SELECT ut.topic FROM UserTopic ut WHERE ut.user.id = :userId AND ut.subscribed = true
    """)
    List<Topic> findSubscribedTopicsByUserId(@Param("userId") Long userId);

    Optional<UserTopic> findByUserAndTopic(User user, Topic topic);

    boolean existsByUserAndTopic(User user, Topic topic);

    List<UserTopic> findBySubscribedFalse();

    void DeleteBySubscribedFalse();
}