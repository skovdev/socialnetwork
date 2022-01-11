package local.socialnetwork.profileservice.service.kafka.consumer.profile;

import lombok.AccessLevel;

import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfileConsumeService {

    static final String PROFILE_GROUP_ID = "default-group-id";
    static final String TOPIC_PROFILE_NEW = "topic.profile.new";

    @KafkaListener(topics = TOPIC_PROFILE_NEW, groupId = PROFILE_GROUP_ID)
    public void consumeProfileForSaving() {

    }
}