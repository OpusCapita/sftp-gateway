package com.opuscapita.bouncer.client.rabbitmq;

import com.opuscapita.bouncer.client.ServiceClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("rabbitmqServiceClient")
public class RabbitMQServiceClient implements ServiceClient {
    @Override
    public void sendEvent(String message) {

    }
}
