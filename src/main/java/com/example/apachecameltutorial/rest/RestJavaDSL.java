package com.example.apachecameltutorial.rest;

import com.example.apachecameltutorial.dto.Weather;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RestJavaDSL extends RouteBuilder {

    private final WeatherDataProvider weatherDataProvider;

    public RestJavaDSL(WeatherDataProvider weatherDataProvider) {
        this.weatherDataProvider = weatherDataProvider;
    }


    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("jetty")
                .host("localhost")
                .port(8082).bindingMode(RestBindingMode.auto);

        rest("/weather")
                .get("/{city}").outType(Weather.class)
                .routeId("Rest-DSL")
                .to("direct:process-weather");

        from("direct:process-weather")
                .process(this::getWeatherData);
    }

    private void getWeatherData(Exchange exchange){
        String city = exchange.getMessage().getHeader("city", String.class);

        Weather currentWeather = weatherDataProvider.getCurrentWeather(city);

        if(Objects.nonNull(currentWeather)){
            Message message = new DefaultMessage(exchange.getContext());
            message.setBody(currentWeather);

            exchange.setMessage(message);
        }
//        else{
//            exchange.getMessage().setHeader(HTTP_RESPONSE_CODE, NOT_FOUND.value());
//        }
    }
}
