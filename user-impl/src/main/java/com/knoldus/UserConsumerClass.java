/*
package com.knoldus;

import akka.Done;
import akka.stream.javadsl.Flow;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserConsumerClass {

    @Inject
    public UserConsumerClass(UserService service) {
        System.out.println("I am inside user service's singleton class");
        service.usersTopic()
                .subscribe()
                .atLeastOnce(Flow.fromFunction(this::processUser));
    }

    private Done processUser(User user) {
        System.out.println("Hello " + user.getName());
        return Done.getInstance();
    }
}
*/
