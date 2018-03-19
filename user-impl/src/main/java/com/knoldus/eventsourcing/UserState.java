package com.knoldus.eventsourcing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.User;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
@JsonDeserialize
@AllArgsConstructor
public class UserState implements CompressedJsonable {
    Optional<User> user;
    String timestamp;
}
