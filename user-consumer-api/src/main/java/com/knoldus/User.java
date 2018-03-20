package com.knoldus;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class User {

    String id;

    String name;

    int age;
}
