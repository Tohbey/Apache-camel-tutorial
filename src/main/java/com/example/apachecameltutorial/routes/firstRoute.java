package com.example.apachecameltutorial.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class firstRoute extends RouteBuilder {

    @Autowired
    private GetCurrentTimeBean getCurrentTimeBean;

    @Autowired
    private SimpleLoggingProcessingComponent loggingComponent;

    @Override
    public void configure() throws Exception {
        /*
         * Events that can be preformed in route can be processing and transformation
         * .transform() -> function used for transformation or .bean()
         * .process() -> function used for processing or bean()
         * */

        // timer
        // transformation
        // log
        // Exchange[ExchangePattern: InOnly, BodyType: null, Body: [Body is null]]
        from("timer:first-timer") //null
                .log("${body}")//null
                .transform().constant("My Constant Message")
                .log("${body}")//My Constant Message
                //.transform().constant("Time now is" + LocalDateTime.now())
                //.bean("getCurrentTimeBean")

                //Processing
                //Transformation

                .bean(getCurrentTimeBean)
                .log("${body}")//Time now is2021-01-18T18:32:19.660244
                .bean(loggingComponent)
                .log("${body}")
                .process(new SimpleLoggingProcessor())
                .to("log:first-timer"); //database
    }
}

@Component
class GetCurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is" + LocalDateTime.now();
    }
}

@Component
class SimpleLoggingProcessingComponent {

    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);

    public void process(String message) {

        logger.info("SimpleLoggingProcessingComponent {}", message);

    }
}

/*
 * a class that implements processor from apache camel to process response from a router.
 * */
class SimpleLoggingProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("SimpleLoggingProcessor {}", exchange.getMessage().getBody());
    }

}
