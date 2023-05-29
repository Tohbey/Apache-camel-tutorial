package com.example.apachecameltutorial.rest;


import com.example.apachecameltutorial.dto.Weather;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.apache.camel.component.rest.RestConstants.HTTP_RESPONSE_CODE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static com.example.apachecameltutorial.rabbitmq.RabbitmqConfiguration.*;


@Component
public class RestJava extends RouteBuilder {

    private final WeatherDataProvider weatherDataProvider;

    public RestJava(WeatherDataProvider weatherDataProvider) {
        this.weatherDataProvider = weatherDataProvider;
    }

    /*
* Implementing Restful webservices on camel
* there are two main flavours to support restful services
* camel Java DSL -> from("rest:api/orders").to("jms:orders)
* Rest DSL -> rest("api").{method}("orders").to("jms:orders")
*
* Workshop:
* 1. Build Java DSL style rest service to get weather details.
* 2. Build Rest DSL style rest service to get weather details.
* 3. Ability to save the data.
* 4. when there is no data then return 400 - Not found.
* 5. also publish an event to RabbitMQ.
* */

    //http://localhost:8080/services/javadsl/weather/{city}
    @Override
    public void configure() throws Exception {
        from("jetty:http://localhost:8082/weather?produces=application/json").
                setHeader(Exchange.HTTP_METHOD, simple("GET")).
                routeId("Rest-GET").
                outputType(Weather.class).
                process(this::getWeatherData);

        from("jetty:http://localhost:8082/weather?consume=application/json").
                setHeader(Exchange.HTTP_METHOD, simple("POST")).
                routeId("Rest-POST").
                log(LoggingLevel.INFO, "Body: ${body}").
                unmarshal().json(JsonLibrary.Jackson, Weather.class).
                to("direct:save-weather-data");

        from("direct:save-weather-data")
                .process(this::saveWeatherData)
                .wireTap("direct:write-to-rabbit")
                .log(LoggingLevel.INFO, "Body: ${body}")
                .end();

        from("direct:write-to-rabbit")
                .log(LoggingLevel.INFO, "Writing to Rabbitmg")
                .routeId("Rest-Rabbit")
                .marshal().json(JsonLibrary.Jackson, Weather.class)
                .toF(RABBIT_URI, QUEUE_WEATHER, ROUTINGKEY_WEATHER_DATA)
                .log(LoggingLevel.INFO, "Writing to File.");

    }

    private void saveWeatherData(Exchange exchange){
        Weather body = exchange.getMessage().getBody(Weather.class);

        weatherDataProvider.setCurrentWeather(body);
    }
    private void getWeatherData(Exchange exchange){
        String city = exchange.getMessage().getHeader("city", String.class);

        Weather currentWeather = weatherDataProvider.getCurrentWeather(city);

        if(Objects.nonNull(currentWeather)){
            Message message = new DefaultMessage(exchange.getContext());
            message.setBody(currentWeather);

            exchange.setMessage(message);
        }
        else{
            exchange.getMessage().setHeader(HTTP_RESPONSE_CODE, NOT_FOUND.value());
        }
    }
}
