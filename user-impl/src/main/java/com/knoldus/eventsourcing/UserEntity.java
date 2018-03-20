package com.knoldus.eventsourcing;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public class UserEntity extends PersistentEntity<UserCommands, UserEvent, UserState> {

    @Override
    public Behavior initialBehavior(Optional<UserState> snapshotState) {

        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                UserState.builder().user(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(UserCommands.CreateUser.class, (cmd, ctx) ->
                ctx.thenPersist(UserEvent.UserCreated.builder().user(cmd.getUser())
                        .entityId(entityId()).build(), evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(UserEvent.UserCreated.class, evt ->
                UserState.builder().user(Optional.of(evt.getUser()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setReadOnlyCommandHandler(UserCommands.GetUser.class, (cmd, ctx) ->
                ctx.reply(state().getUser())
        );

        return behaviorBuilder.build();
    }
}
