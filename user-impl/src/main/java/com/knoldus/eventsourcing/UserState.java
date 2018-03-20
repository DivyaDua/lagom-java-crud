package com.knoldus.eventsourcing;

import com.knoldus.User;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class UserState implements CompressedJsonable {

    Optional<User> user;

    String timestamp;

}
