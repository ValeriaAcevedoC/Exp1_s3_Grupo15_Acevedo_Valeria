package com.duoc.guias.service;

import com.duoc.guias.model.GuiaDespacho;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CreacionGuiaConsumerService {

    private final GuiaService guiaService;
    private final ProductorService productorService;

    public CreacionGuiaConsumerService(
            GuiaService guiaService,
            ProductorService productorService) {
        this.guiaService = guiaService;
        this.productorService = productorService;
    }

    @RabbitListener(queues = "${rabbitmq.cola.creacion-guias}")
    public void crearGuiaDesdeCola(GuiaDespacho guia) {
        try {
            guiaService.guardar(guia);
        } catch (Exception exception) {
            productorService.enviarError(
                    guia,
                    "No fue posible crear la guia desde la cola de creacion: "
                            + exception.getMessage()
            );
        }
    }
}
