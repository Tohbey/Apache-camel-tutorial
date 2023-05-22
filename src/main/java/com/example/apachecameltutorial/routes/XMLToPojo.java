package com.example.apachecameltutorial.routes;

import com.example.apachecameltutorial.dto.TravelerInformationResponse;
import jakarta.xml.bind.JAXBContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.INFO;

@Component
public class XMLToPojo extends RouteBuilder {



    @Override
    public void configure() throws Exception {
//      XML data format
        JAXBContext context = JAXBContext.newInstance(TravelerInformationResponse.class);
        JaxbDataFormat jaxbDataFormat = new JaxbDataFormat();

        jaxbDataFormat.setContext(context);
        jaxbDataFormat.setPrettyPrint(true);

        from("direct:http-1")
                .log(INFO, "This will be second Message!")
                .to("http://restapi.adequateshop.com/api/Traveler")
                .unmarshal(jaxbDataFormat)
                .log(INFO, this.log,"XML format: ${body}")
                .marshal().json(JsonLibrary.Jackson, TravelerInformationResponse.class)
                .log(INFO, this.log,"POJO format: ${body}");
    }
}
