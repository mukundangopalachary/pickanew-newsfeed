package app.news.backend.service;

import app.news.backend.exception.TopicNotFoundException;
import app.news.backend.model.Topic;
import app.news.backend.repository.TopicRepository;
import app.news.backend.repository.UserTopicRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO
// 1. subscribe
// 2. unsubscribe

@Service
public class TopicService {

  @Autowired private TopicRepository topicRepository;

  @Autowired private UserTopicRepository userTopicRepository;

  public List<Topic> getTopics() {
    return topicRepository.findAll();
  }

  public Topic getTopicById(Long id) throws TopicNotFoundException {
    if (topicRepository.existsById(id)) throw new TopicNotFoundException("Topic does not exist");

    Topic topic = topicRepository.findTopicById(id);

    return topic;
  }
}
