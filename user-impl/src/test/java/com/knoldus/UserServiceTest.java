package com.knoldus;

import akka.Done;
import com.datastax.driver.core.Session;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.TestServer;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class UserServiceTest {

    private static TestServer testServer;
    private static UserService userService;

    @BeforeClass
    public static void setUp() throws InterruptedException, ExecutionException, TimeoutException {
        testServer = startServer(defaultSetup().withCassandra(true));
        CassandraSession cassandraSession = testServer.injector().instanceOf(CassandraSession.class);
        Session session = cassandraSession.underlying().toCompletableFuture().get(45, SECONDS);
        createSchema(session);
        populateData(session);
        userService = testServer.client(UserService.class);

    }

    @AfterClass
    public static void tearDown() {
        if (testServer != null) {
            testServer.stop();
            testServer = null;
        }
    }

    private static void createSchema(Session session) {
        session.execute("CREATE KEYSPACE IF NOT EXISTS user_service WITH replication = "
                + "{'class': 'SimpleStrategy', 'replication_factor': '1'}");
        session.execute("CREATE TABLE IF NOT EXISTS user_service.user (id TEXT, name TEXT, age INT, PRIMARY KEY(id))");
    }

    private static void populateData(Session session) {
        session.execute("INSERT INTO user_service.user (id, name, age) VALUES ('1', 'testUser', 10)");
    }

    @Test
    public void shouldCreateUser() throws Exception {
        User user = User.builder().id("1").name("testUser").age(10).build();
        CompletableFuture<Done> doneCompletableFuture = userService.createUser().invoke(user).toCompletableFuture();
        assertEquals("failed to create user", Done.getInstance(), doneCompletableFuture.get(5, SECONDS));
    }

    @Test
    public void shouldGetUser() throws Exception {
        Optional<User> expectedUser = Optional.of(User.builder().id("1").name("testUser").age(10).build());
        CompletableFuture<Optional<User>> optionalUser = userService.getUser("1").invoke().toCompletableFuture();
        assertEquals("getUser fails to return expected user", expectedUser, optionalUser.get(15, SECONDS));
    }
}
