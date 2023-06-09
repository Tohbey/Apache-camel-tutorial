package com.example.apachecameltutorial.dto;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "travelers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Travelers {

    @XmlElement(name = "Travelerinformation")
    private List<TravelerInformation> travelerinformation;
}
