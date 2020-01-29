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
import org.acme.ConfigJsonObject;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 *
 * @author Mathias
 */


    @ApplicationScoped
    public class EntryConsumer {
        
        @Inject
        @Channel("routing")
        Emitter<String> outgoing;
        
        @Inject
        ProducerTemplate camelProducer;
        
        @Incoming("entry")
        //@Outgoing("routing")
        public void consumeEntry(String msg) throws IOException, InterruptedException, ExecutionException, TimeoutException {
            
            ConfigJsonObject cj = new ConfigJsonObject("entry");
            cj.convertJsonToEntity(msg);
            String newMsg = cj.getJsonObjectString();
            
            //Store in database
            System.out.println("\n------------- Sending entry to database... Time: " + LocalTime.now()+ "-------------\n");
            CompletableFuture future = camelProducer.asyncSendBody("couchdb:http://cis-x.convergens.dk:5984/mmr?username=admin&password=password", newMsg);
            future.get(5, TimeUnit.SECONDS);
            if (future.isDone()){
                System.out.println("\n------------- Succesfully sent to database -------------\n");
                outgoing.send(newMsg);
            } else {
                System.out.println("ELSE ---------------");
                //TODO find ud af hvad der skal ske
            }

        }

    }



