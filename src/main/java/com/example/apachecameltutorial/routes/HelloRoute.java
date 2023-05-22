package com.example.apachecameltutorial.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "example.apachecameltutorial.hello.enabled", havingValue = "true")
public class HelloRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:greeting")
                .log("Hello ${body}")
                .choice()
                    .when().simple("${body} contains 'Team'")
                        .log("I like working with teams")
                    .otherwise()
                       .log("Solo Fighters")
                .end();
    }
}
