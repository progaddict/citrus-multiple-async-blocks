package org.fixiewixie;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.builder.RepeatOnErrorBuilder;
import com.consol.citrus.dsl.runner.AbstractTestBehavior;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.server.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.*;

public class FixieWixie extends TestNGCitrusTestRunner {

    @Autowired
    private HttpServer server;

    @Autowired
    private HttpClient client;

    @Override
    public RepeatOnErrorBuilder repeatOnError() {
        return super.repeatOnError()
                .until((i, testContext) -> i > 10)
                .autoSleep(1000);
    }

    @Test
    @CitrusTest
    public void testSubmitJob_works() {
        clear();

        applyBehavior(new ClientSendReceive(client, "foo"));

        applyBehavior(new MockServerReceiveRespond(server, "foo"));

        repeatOnError().actions(
                sequential().actions(
                        http(b -> b.client(client).send().get().path("/completed")),
                        http(b -> b.client(client).receive().response(HttpStatus.OK).payload("[\"foo\"]"))
                )
        );
    }

    @Test
    @CitrusTest
    public void testSubmit2Jobs_works() {
        clear();

        applyBehavior(new ClientSendReceive(client, "foo"));

        applyBehavior(new ClientSendReceive(client, "boo"));

        applyBehavior(new MockServerReceiveRespond(server, "boo"));

        applyBehavior(new MockServerReceiveRespond(server, "foo"));

        repeatOnError().actions(
                sequential().actions(
                        http(b -> b.client(client).send().get().path("/completed")),
                        http(b -> b.client(client).receive().response(HttpStatus.OK).payload("[\"boo\", \"foo\"]"))
                )
        );
    }

    @Test
    @CitrusTest
    public void testSubmitJobAsync_works() {
        clear();

        async().actions(
                applyBehavior(new ClientSendReceive(client, "fuchsie-wuchsie")),
                applyBehavior(new MockServerReceiveRespond(server, "fuchsie-wuchsie"))
        );

        sleep(15000);
        repeatOnError().actions(
                sequential().actions(
                        http(b -> b.client(client).send().get().path("/completed")),
                        http(b -> b.client(client).receive().response(HttpStatus.OK).payload("[\"fuchsie-wuchsie\"]"))
                )
        );
    }

    @Test
    @CitrusTest
    public void testSubmitJobAsync_doesNotWork() {
        clear();

        async().actions(
                sequential().actions(
                        applyBehavior(new ClientSendReceive(client, "fuchsie-wuchsie")),
                        applyBehavior(new MockServerReceiveRespond(server, "fuchsie-wuchsie")),
                        sequential().actions(
                                http(b -> b.client(client).send().get().path("/completed")),
                                http(b -> b.client(client).receive().response(HttpStatus.OK).payload("[\"fuchsie-wuchsie\"]"))
                        )
                )
        );
    }

    @Test
    @CitrusTest
    public void testSubmit2JobsAsync_doesNotWork() {
        clear();

        async().actions(
                applyBehavior(new ClientSendReceive(client, "foo")),
                applyBehavior(new MockServerReceiveRespond(server, "foo"))
        );

        async().actions(
                applyBehavior(new ClientSendReceive(client, "boo")),
                applyBehavior(new MockServerReceiveRespond(server, "boo"))
        );
    }

    private void clear() {
        http(b -> b.client(client).send().delete());
        http(b -> b.client(client).receive().response(HttpStatus.NO_CONTENT));
    }

    private static class ClientSendReceive extends AbstractTestBehavior {

        private final HttpClient client;
        private final String jobName;

        private ClientSendReceive(final HttpClient client, final String jobName) {
            this.client = client;
            this.jobName = jobName;
        }

        @Override
        public void apply() {
            http(b -> b.client(client).send().post().queryParam("name", jobName));
            http(b -> b.client(client).receive().response(HttpStatus.OK));
        }
    }

    private static class MockServerReceiveRespond extends AbstractTestBehavior {

        private final HttpServer server;
        private final String jobName;

        private MockServerReceiveRespond(final HttpServer server, final String jobName) {
            this.server = server;
            this.jobName = jobName;
        }

        @Override
        public void apply() {
            Map<String, Object> selectors = new HashMap<>();
            selectors.put("jsonPath:$.job", jobName);
            http(b -> b.server(server).receive().post().selector(selectors));
            http(b -> b.server(server).respond().status(HttpStatus.OK));
        }
    }
}
