package com.opuscapita.bouncer.client.kafka;

import com.opuscapita.bouncer.client.ServiceClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Qualifier("kafkaServiceClient")
@Primary
public class KafkaServiceClient implements ServiceClient {
}
