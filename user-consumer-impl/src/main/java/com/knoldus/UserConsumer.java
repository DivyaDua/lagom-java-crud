package com.knoldus;

import akka.Done;
import akka.stream.javadsl.Flow;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserConsumer {

    @Inject
    public UserConsumer(UserConsumerService service) {
        service.userConsumerTopic()
                .subscribe()
                .atLeastOnce(Flow.fromFunction(this::processUser));
    }

    private Done processUser(User user) {
        System.out.println("Hello " + user.getName());
        return Done.getInstance();
    }
}
