package com.example.apachecameltutorial.routes;


import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class AggregatorRoute extends RouteBuilder {
    final String CORRELATION_ID = "correlationId";

    /*
     * what is Aggregator: aggregator combines many related incoming messages into a single aggregated message.
     * it receives a stream of messages and identifies messages that are related and combines it into a single aggregated message.
     * Once the aggregation is complete, it sends it out to the other channels.
     *
     * Must have configuration for aggregator design pattern.
     * 1. Correlation identifier: an expression that determines which incoming messages belong together.
     * 2. Completion condition: A predicate or time-based condition that determines when the result message should be sent.
     * 3. Aggregation strategy: An aggregation strategy that specifies how to combine the messages into a single message.
     *
     *
     * */

    @Override
    public void configure() throws Exception {
        Random random = new Random();
        from("timer:insurance?period=1000")
                .process(exchange ->
                {
                    Message message = exchange.getMessage();
                    message.setHeader(CORRELATION_ID, random.nextInt(4));
                    message.setBody(new Date().toString());
                })
                .log(LoggingLevel.INFO, "${header."+CORRELATION_ID+"} ${body}")
                .aggregate(header(CORRELATION_ID), new MyAggregationStrategy())
                .completionSize(3)
                .log(LoggingLevel.INFO, "${header."+CORRELATION_ID+"} ${body}");
    }
}
