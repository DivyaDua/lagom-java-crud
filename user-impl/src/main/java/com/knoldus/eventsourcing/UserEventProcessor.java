package com.knoldus.eventsourcing;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import com.knoldus.eventsourcing.UserEvent.UserCreated;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class UserEventProcessor extends ReadSideProcessor<UserEvent> {

    private final CassandraSession cassandraSession;
    private final CassandraReadSide cassandraReadSide;

    private PreparedStatement createUserStmt;

    /**
     * UserEventProcessor Constructor.
     * @param session - Cassandra Session
     * @param readSide - Cassandra ReadSide
     */
    @Inject
    public UserEventProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.cassandraSession = session;
        this.cassandraReadSide = readSide;
    }

    /**
     * Build handler.
     * @return ReadSideHandler of UserEvent
     */
    @Override
    public ReadSideHandler<UserEvent> buildHandler() {
        return cassandraReadSide.<UserEvent>builder("User_offsets")
                .setGlobalPrepare(this::createTable)
                .setPrepare(userEventAggregateEventTag -> prepareCreateUserStmt())
                .setEventHandler(UserEvent.UserCreated.class, this::processCreateUser)
                .build();
    }

    /**
     * Aggregate Event Tags.
     * @return UserEvent
     */
    @Override
    public PSequence<AggregateEventTag<UserEvent>> aggregateTags() {
        return TreePVector.singleton(UserEventTag.INSTANCE);
    }

    /**
     * Create user table.
     * @return Done
     */
    private CompletionStage<Done> createTable() {
        return cassandraSession.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS user_service.user ("
                         + "id TEXT, name TEXT, age INT, PRIMARY KEY(id))"
        );
    }

    /**
     * Prepare Insert Statements.
     * @return Done
     */
    private CompletionStage<Done> prepareCreateUserStmt() {
        return cassandraSession.prepare(
                "INSERT INTO user_service.user (id, name, age) VALUES (?, ?, ?)"
        ).thenApply(preparedStatement -> {
            this.createUserStmt = preparedStatement;
            return Done.getInstance();
        });
    }

    /**
     * Bind user values to insert statement.
     * @param userCreated UserCreated event
     * @return List of BoundStatement
     */
    private CompletionStage<List<BoundStatement>> processCreateUser(UserCreated userCreated) {
        BoundStatement bindCreateUserStmt = createUserStmt.bind();
        bindCreateUserStmt.setString("id", userCreated.getUser().getId());
        bindCreateUserStmt.setString("name", userCreated.getUser().getName());
        bindCreateUserStmt.setInt("age", userCreated.getUser().getAge());

        return CassandraReadSide.completedStatements(
                Collections.singletonList(bindCreateUserStmt));
    }
}
