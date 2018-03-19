package com.knoldus;

import akka.Done;
import akka.stream.javadsl.Flow;

import javax.inject.Inject;

/*public class UserConsumerServiceImpl implements UserConsumerService {

    private final UserService userService;

    @Inject
    public UserConsumerServiceImpl(UserService service) {
        this.userService = service;

        System.out.println("\n\nI am here");
        userService.usersTopic()
                .subscribe()
                .atLeastOnce(Flow.fromFunction(this::processUser));
    }

    private Done processUser(User user) {
        System.out.println("Hello " + user.getName());
        return Done.getInstance();
    }
}*/

