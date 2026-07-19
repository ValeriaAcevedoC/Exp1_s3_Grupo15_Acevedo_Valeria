package com.duoc.guiasbff.service;

import com.duoc.guias.model.GuiaDespacho;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GuiaPublisherService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.guias}")
    private String exchangeGuias;

    @Value("${rabbitmq.routing-key.creacion-guias}")
    private String routingKeyCreacionGuias;

    public GuiaPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarGuia(GuiaDespacho guia) {
        rabbitTemplate.convertAndSend(
                exchangeGuias,
                routingKeyCreacionGuias,
                guia
        );
    }
}
