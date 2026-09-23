package app.news.backend.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_topics")
public class UserTopic {
  
  @EmbeddedId
  private UserTopicId userTopicId;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("topicId")
  @JoinColumn(name = "topic_id", nullable = false)
  private Topic topic;

  @Column(name = "subscribed_at", nullable = false)
  private OffsetDateTime subscribedAt;

  @Column(name = "subscribed", nullable = false)
  private boolean subscribed = true;
}
