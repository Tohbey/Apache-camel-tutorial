package com.example.apachecameltutorial.routes;

import com.example.apachecameltutorial.dto.Weather;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.DefaultRoute;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.example.apachecameltutorial.rabbitmq.RabbitmqConfiguration.*;
import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.INFO;


@Component
public class WeatherRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        // Called by Rabbit on message in weather queue
        /*
        {
         "city": "London",
         "temp": "20",
         "unit": "C"
        }
        */
        fromF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER)
                .routeId("weather")
                .log(INFO, "Headers: ${headers}")
                .log(INFO, "Before Enrichment: ${body}")
                .unmarshal()
                .json(JsonLibrary.Jackson, Weather.class)
                .process(this::enrichWeatherDto)
                .log(INFO, "After Enrichment: ${body}")
                .marshal()
                .json(JsonLibrary.Jackson, Weather.class)
                .log(INFO, "${body}")
                .toF(RABBIT_URI, QUEUE_WEATHER_EVENTS, QUEUE_WEATHER_EVENTS);

        /*
         * The following queue can be used to update the Weather route. Simply send one the following
         * to weather-command route: START / STOP / RESUME / SUSPEND
        */
        fromF(RABBIT_URI, "weather-command", "weather-command")
                .process(
                        p -> {
                            String command = p.getMessage().getBody(String.class).toUpperCase();

                            System.out.println("Request for Weather Route: " + command);
                            DefaultCamelContext context = (DefaultCamelContext) getContext();

                            if (command.equals("SUSPEND")) {
                                context.suspendRoute("weather");
                            } else if (command.equals("RESUME")) {
                                context.resumeRoute("weather");
                            } else if (command.equals("STOP")) {
                                context.stopRoute("weather");
                            } else if (command.equals("START")) {
                                context.startRoute("weather");
                            }

                            DefaultRoute weather = (DefaultRoute) context.getRoute("weather");

                            System.out.println("State of Weather Route: " + weather.getStatus());
                        }
                );
    }

    private void enrichWeatherDto(Exchange exchange) {
        MDC.put("JSS", "Str-1");
        Weather dto = exchange.getMessage().getBody(Weather.class);
        dto.setReceivedTime(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}
