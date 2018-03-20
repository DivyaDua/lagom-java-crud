package com.knoldus;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.broker.Topic;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.topic;

public interface UserConsumerService extends Service {

    String USERS_TOPIC = "user";

    Topic<User> userConsumerTopic();

    @Override
    default Descriptor descriptor() {
        return named("user-consumer-service")
                .withTopics(
                        topic(USERS_TOPIC, this::userConsumerTopic)
                ).withAutoAcl(true);
    }
}
