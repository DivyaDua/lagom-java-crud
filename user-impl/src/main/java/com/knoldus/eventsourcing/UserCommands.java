package com.knoldus.eventsourcing;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.User;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

public interface UserCommands extends Jsonable {

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class CreateUser implements UserCommands, PersistentEntity.ReplyType<Done> {
        User user;
    }

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class UserCurrentState implements UserCommands, PersistentEntity.ReplyType<Optional<User>> {}
}
