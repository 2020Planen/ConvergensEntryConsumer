/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.consumer;

import io.smallrye.reactive.messaging.annotations.Broadcast;
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
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

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
            
            //Store in database
            System.out.println("Sending entry to database... Time: " + LocalTime.now());
            CompletableFuture future = camelProducer.asyncSendBody("couchdb:http://cis-x.convergens.dk:5984/mmr?username=admin&password=password", msg);
            System.out.println("Pre object = ");
            Object result = future.get(5, TimeUnit.SECONDS);
            System.out.println("after object");
            if (future.isDone()){
                System.out.println("result: " + result.toString());
                outgoing.send(msg);
            }
/*          
            try {
                context.addRoutes(new RouteBuilder() {
                        
                    @Override
                    public void configure() throws Exception {
                        from("kafka:entry?brokers=cis-x.convergens.dk:9092")
                                .setBody(e -> msg)
                                .to("couchdb:http://cis-x.convergens.dk:5984/mmr?username=admin&password=password");
                                
                        System.out.println("------------ adding to couch db ---------------------");
                    }
                });
                context.start();
                
            } catch (Exception e) {
                System.out.println(""+e);
            } finally {
            }
*/
        }

    }



