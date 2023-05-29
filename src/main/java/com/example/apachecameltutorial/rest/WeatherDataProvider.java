package com.example.apachecameltutorial.rest;

import com.example.apachecameltutorial.dto.Weather;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherDataProvider {

    private static Map<String, Weather> weatherMap = new HashMap<>();

    public WeatherDataProvider(){
        Weather dto = Weather.builder().city("London").temp("10").unit("C")
                .receivedTime(new Date().toString()).id(1).build();

        weatherMap.put("LONDON", dto);
    }

    public Weather getCurrentWeather(String city){
        return weatherMap.get(city.toUpperCase());
    }

    public void setCurrentWeather(Weather dto){
        dto.setReceivedTime(new Date().toString());
        weatherMap.put(dto.getCity().toUpperCase(), dto);
    }
}
