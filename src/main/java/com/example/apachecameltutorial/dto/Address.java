package com.example.apachecameltutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private Integer id;
    private String street;

    private String streetName;

    private Integer buildingNumber;

    private String city;

    private String zipcode;

    private String country;

    private String county_code;
    private float latitude;
    private float longitude;

}
