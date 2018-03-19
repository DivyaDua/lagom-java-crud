package com.knoldus;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.knoldus.eventsourcing.UserCommands;
import com.knoldus.eventsourcing.UserCommands.CreateUser;
import com.knoldus.eventsourcing.UserEntity;
import com.knoldus.eventsourcing.UserEvent;
import com.knoldus.eventsourcing.UserEvent.UserCreated;
import com.knoldus.eventsourcing.UserState;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class UserEntityTest {

    static ActorSystem system;

    @BeforeClass
    public static void setUp() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testCreateUser() {
        PersistentEntityTestDriver<UserCommands, UserEvent, UserState> driver =
                new PersistentEntityTestDriver<>(system, new UserEntity(), "testId");

        User user = User.builder().id("1").name("testUser").age(10).build();
        Outcome<UserEvent, UserState> outcome = driver.run(CreateUser.builder().user(user).build());

        assertEquals(UserCreated.builder().entityId("testId").user(user).build(),
                outcome.events().get(0));
        assertEquals(1, outcome.events().size());
        assertEquals(Optional.of(user), outcome.state().getUser());
        assertEquals(Done.getInstance(), outcome.getReplies().get(0));
        assertEquals(Collections.emptyList(), outcome.issues());
    }

}
