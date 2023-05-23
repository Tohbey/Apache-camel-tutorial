package com.example.apachecameltutorial.rabbitmq;

import org.apache.camel.Configuration;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@Configuration
public class RabbitmqConfiguration {
    public static final String QUEUE_WEATHER = "weather";
    public static final String QUEUE_WEATHER_EVENTS = "weather-events";
    public static String EXCHANGE_WEATHER_DATA = "weather.data";
    public static final String RABBIT_URI =
            "spring-rabbitmq:" + EXCHANGE_WEATHER_DATA + "?queues=%s&routingKey=%s&arg.queue.autoDelete=false&autoDeclare=true";
    public static String QUEUE_WEATHER_DATA = "weather-data";
    public static String ROUTINGKEY_WEATHER_DATA = "weather-data";
    public static String RMQ_HOST = "rmq.host";
    public static String RMQ_PORT = "rmq.port";

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory2() {
        return factory();
    }

    public CachingConnectionFactory factory() {
        Properties properties = System.getProperties();
        String host = properties.getProperty(RMQ_HOST, "localhost");
        String port = properties.getProperty(RMQ_PORT, "15672");
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses("localhost:15672,localhost:15672");
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }

    @Bean
    public Queue weatherData() {
        return new Queue(QUEUE_WEATHER_DATA);
    }

    @Bean
    public Exchange weatherDirectExchange() {
        return new DirectExchange(EXCHANGE_WEATHER_DATA);
    }

    @Bean
    public Binding weatherDataBinding() {
        return BindingBuilder
                .bind(weatherData())
                .to(weatherDirectExchange())
                .with(ROUTINGKEY_WEATHER_DATA)
                .noargs();
    }
}
