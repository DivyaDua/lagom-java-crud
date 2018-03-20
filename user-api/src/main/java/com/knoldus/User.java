package com.knoldus;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class User implements Jsonable {
    private static final long serialVersionUID = 1L;

    String id;

    String name;

    int age;
}
