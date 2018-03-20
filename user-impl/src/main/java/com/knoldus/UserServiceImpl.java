package com.knoldus;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.knoldus.eventsourcing.*;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import javax.inject.Inject;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession session;

    /**
     * Constructor.
     *
     * @param registry         - PersistentEntityRegistry
     * @param readSide         - Cassandra ReadSide
     * @param cassandraSession - Cassandra Session
     */
    @Inject
    public UserServiceImpl(final PersistentEntityRegistry registry,
                           final CassandraSession cassandraSession,
                           ReadSide readSide) {
        this.persistentEntityRegistry = registry;
        this.session = cassandraSession;

        persistentEntityRegistry.register(UserEntity.class);
        readSide.register(UserEventProcessor.class);
    }

    /**
     * User Entity Ref.
     *
     * @param id identifier
     * @return persistent entity ref
     */
    private PersistentEntityRef<UserCommands> userEntityRef(String id) {
        return persistentEntityRegistry.refFor(UserEntity.class, id);
    }

    @Override
    public Topic<User> usersTopic() {
        return TopicProducer.singleStreamWithOffset(offset -> persistentEntityRegistry
                .eventStream(UserEventTag.INSTANCE, offset)
                .map(this::convertEvent));
    }

    private Pair<User, Offset> convertEvent(Pair<UserEvent, Offset> userEventAndOffset) {
        if (userEventAndOffset.first() instanceof UserEvent.UserCreated) {
            UserEvent.UserCreated userCreated = (UserEvent.UserCreated) userEventAndOffset.first();
            User user = userCreated.getUser();
            return Pair.create(user, userEventAndOffset.second());
        } else {
            throw new IllegalArgumentException("Unknown event: " + userEventAndOffset.first());
        }

    }

    @Override
    public ServiceCall<NotUsed, Optional<User>> getUser(String id) {
        return request -> session.selectOne("SELECT * FROM user_service.user WHERE id = ?", id)
                .thenApply(row -> row.map(row1 -> User.builder().id(row1.getString("id"))
                        .name(row1.getString("name"))
                        .age(row1.getInt("age")).build()));
    }

    @Override
    public ServiceCall<User, Done> createUser() {
        return user -> {
            PersistentEntityRef<UserCommands> ref = userEntityRef(user.getId());
            return ref.ask(UserCommands.CreateUser.builder().user(user).build());
        };
    }


}
