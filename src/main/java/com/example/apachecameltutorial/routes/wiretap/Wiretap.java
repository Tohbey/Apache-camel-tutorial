package com.example.apachecameltutorial.routes.wiretap;

import com.example.apachecameltutorial.dto.Transaction;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.example.apachecameltutorial.rabbitmq.RabbitmqConfiguration.RABBIT_URI;

@Component
public class Wiretap extends RouteBuilder {
    public static final String SENDER="sender";
    public static final String RECEIVER="receiver";
    public static final String AUDIT_TRANSACTION_ROUTE="direct:audit-transactions";
    public static final String AUDIT="audit-transactions";

    @Override
    public void configure() throws Exception {
        // Called by Rabbit on message in sender queue

        /*
        * {
              "transactionId": "String",
              "senderAccountId": "String",
              "receiverAccountId": "String",
              "amount": "String",
              "currency": "String",
              "transactionDate": "String"
           }
        * */
        fromF(RABBIT_URI, SENDER, SENDER)
                .log("level 1 ${body}")
                .routeId("wiretap")
                .unmarshal().json(JsonLibrary.Jackson, Transaction.class)
                .log("level 2 ${body}")
                .wireTap(AUDIT_TRANSACTION_ROUTE)
                .process(this::enrichTransactionDto)
                .log("level 6 ${body}")
                .marshal().json(JsonLibrary.Jackson,Transaction.class)
                .log("level 7 ${body}")
                .toF(RABBIT_URI, RECEIVER, RECEIVER)
                .log(LoggingLevel.INFO, "Money Transferred ${body}");

        from(AUDIT_TRANSACTION_ROUTE)
                .routeId("Wiretap Audit")
                .process(this::enrichTransactionDto)
                .log("level 3 ${body}")
                .marshal().json(JsonLibrary.Jackson, Transaction.class)
                .log("level 4 ${body}")
                .toF(RABBIT_URI, AUDIT, AUDIT)
                .log("level 5 ${body}");
    }


    private void enrichTransactionDto(Exchange exchange){
        Transaction transaction = exchange.getMessage().getBody(Transaction.class);
        transaction.setTransactionDate(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(transaction);
        exchange.setMessage(message);
    }
}
