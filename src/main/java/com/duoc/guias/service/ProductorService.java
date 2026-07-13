package com.duoc.guias.service;

import com.duoc.guias.model.GuiaDespacho;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProductorService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.guias}")
    private String exchangeGuias;

    @Value("${rabbitmq.routing-key.guias}")
    private String routingKeyGuias;

    @Value("${rabbitmq.routing-key.errores}")
    private String routingKeyErrores;

    public ProductorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarGuia(GuiaDespacho guia) {
        rabbitTemplate.convertAndSend(
                exchangeGuias,
                routingKeyGuias,
                guia
        );

        System.out.println(
                "Guía enviada a RabbitMQ: " + guia.getNumeroGuia()
        );
    }

    public void enviarError(GuiaDespacho guia, String motivo) {
        MensajeError mensajeError = new MensajeError(
                guia,
                motivo
        );

        rabbitTemplate.convertAndSend(
                exchangeGuias,
                routingKeyErrores,
                mensajeError
        );

        System.err.println(
                "Guía enviada a la cola de errores: " + motivo
        );
    }

    public record MensajeError(
            GuiaDespacho guia,
            String motivo
    ) {
    }
}