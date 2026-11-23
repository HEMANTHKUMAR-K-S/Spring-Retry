package org.demo.springretry.client;


import org.apache.logging.log4j.message.Message;
import org.demo.springretry.exception.InventoryUnavaiableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InventoryClient {


    private final AtomicInteger attemptCounter = new AtomicInteger(0);

    private final Logger log = LoggerFactory.getLogger(InventoryClient.class);



    private final RestClient restClient;

    public InventoryClient(RestClient restClient) {
        this.restClient = restClient;
    }



    public String fetchStock(String productId) {

        try {

            return restClient.get()
                    .uri("/product/{productId}",productId)
                    .retrieve()
                    .onStatus(
                            status -> status.is5xxServerError(),
                            (request, response) -> {
                                throw new InventoryUnavaiableException(
                                        "Inventory API temporarily unavailable. HTTP status: "
                                                + response.getStatusCode()
                                );
                            }
                    )
                    .body(String.class);
        }
        catch (ResourceAccessException e){
                throw  new InventoryUnavaiableException("unable to connect with the server"+e.getMessage());
        }













//
//        int attempt = attemptCounter.incrementAndGet();
//
//        log.info("Calling external inventory API. Attempt: {}", attempt);
//
//        if (attempt <= 4) {
//
//                throw new InventoryUnavaiableException(
//                        "Inventory server is temporarily unavailable"
//                );
//
//        }
//
    }
}
