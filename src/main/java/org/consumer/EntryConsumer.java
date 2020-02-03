/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.consumer;

import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.acme.PlayGround;

import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 *
 * @author Mathias
 */
@ApplicationScoped
public class EntryConsumer {

    @Incoming("entry")

    public void handle(String msg) {
        PlayGround cj = new PlayGround("entry", msg);
        try {
            consumeEntry(msg, cj);
        } catch (Exception e) {
            System.out.println("----------- in catch --------------- \n\n");
            cj.handleError(e.getLocalizedMessage());

        }
    }

    @Inject
    @Channel("routing")
    Emitter<String> outgoing;

    @Inject
    ProducerTemplate camelProducer;

    public void consumeEntry(String msg, PlayGround cj) throws IOException, InterruptedException, ExecutionException, TimeoutException, Exception {

        
        String newMsg = cj.getJsonMessage().toString();

        //Store in database
        System.out.println("\n------------- Sending entry to database... Time: " + LocalTime.now() + "-------------\n");
        CompletableFuture future = camelProducer.asyncSendBody("couchdb:http://cis-x.convergens.dk:5984/mmr?username=admin&password=password", newMsg);
        System.out.println(" future resolved "+ future.get(5, TimeUnit.SECONDS));
        if (future.isDone()) {
            System.out.println("\n------------- Succesfully sent to database -------------\n");
            outgoing.send(newMsg);
        } else {
            System.out.println("Ikke sendt til databasen ---------------");
            //TODO find ud af hvad der skal ske
        }

    }

}
