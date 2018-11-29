package org.fixiewixie;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.server.HttpServer;
import org.springframework.context.annotation.Bean;

public class CitrusConfiguration {

    @Bean
    public HttpServer server() {
        return CitrusEndpoints.http()
                .server()
                .port(8085)
                .autoStart(true)
                .timeout(30000)
                .build();
    }

    @Bean
    public HttpClient client() {
        return CitrusEndpoints.http()
                .client()
                .requestUrl("http://localhost:8080/job")
                .timeout(30000)
                .build();
    }
}
