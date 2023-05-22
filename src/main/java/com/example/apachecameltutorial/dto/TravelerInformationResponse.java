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
@XmlRootElement(name = "TravelerinformationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TravelerInformationResponse {

    @XmlElement(name = "page")
    private Integer page;

    @XmlElement(name = "per_page")
    private Integer perPage;

    @XmlElement(name = "totalrecord")
    private Integer totalRecord;

    @XmlElement(name = "total_pages")
    private Integer totalPages;

    @XmlElement(name = "travelers")
    private Travelers travelers;
}
