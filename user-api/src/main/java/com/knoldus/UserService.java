package com.knoldus;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface UserService extends Service {

    String USERS_TOPIC = "user";

    Topic<User> usersTopic();

    /**
     * @return User response.
     */
    ServiceCall<NotUsed, Optional<User>> getUser(String id);

    /**
     * @return Done.
     */
    ServiceCall<User, Done> createUser();

    @Override
    default Descriptor descriptor() {
        return named("user-service").withCalls(
                restCall(Method.GET, "/api/user/:id", this::getUser),
                restCall(Method.POST, "/api/new-user", this::createUser)
        ).withTopics(
                topic(USERS_TOPIC, this::usersTopic)
        ).withAutoAcl(true);
    }

}
