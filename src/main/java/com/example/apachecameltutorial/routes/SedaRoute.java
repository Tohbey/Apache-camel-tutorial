package com.example.apachecameltutorial.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultMessage;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class SedaRoute extends RouteBuilder {

/*
* SEDA -> stage event-driven architecture
*
* 1. refers to an approach to software architecture that decomposes a complex,
* event-driven application into a set of stages connected by queues.
*
* 2. it avoids the high overhead associated with thread-based concurrency models such as (locking
* unlocking and polling) and decouples event and thread scheduling from application logic.
*
* SEDA CAMEL COMPONENT
* it provides asynchronous SEDA behavior.
*
* messages are exchanged on a blocking queue and consumers are invoked in a separate thread from the producer
*
* Queues are only visible within a single CamelContext
*
* the components does not implement any kind of persistence or recovery
*
* url format -> seda:some-name?options
*
* it makes a synchronous code works asynchronously
* */
    @Override
    public void configure() throws Exception {
        from("timer:ping?period=200")
                .routeId("SedaRoute 1")
                .process(exchange -> {
                    Message message = new DefaultMessage(exchange);
                    message.setBody(new Date());
                    exchange.setMessage(message);
                })
                .to("seda:weightshifter");


        from("seda:weightshifter?multipleConsumers=true").
                to("direct:complexProcess");

        from("direct:complexProcess")
                .log(LoggingLevel.INFO, this.log, "${body}")
                .process(exchange -> SECONDS.sleep(2))
                .end();
    }
}
