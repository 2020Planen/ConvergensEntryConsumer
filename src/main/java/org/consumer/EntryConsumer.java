/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 *
 * @author Mathias
 */


    @ApplicationScoped
    public class EntryConsumer {
        
        @Inject
        ProducerTemplate camelProducer;
        
        @Incoming("entry")
        public void consumeEntry(String msg) throws IOException {
            
            //Store in database
            System.out.println("Sending entry to database...");
            camelProducer.sendBody("couchdb:http://cis-x.convergens.dk:5984/mmr?username=admin&password=password", msg);
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



