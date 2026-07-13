package com.duoc.guias.service;

import com.duoc.guias.model.GuiaDespacho;
import com.duoc.guias.model.GuiaProcesada;
import com.duoc.guias.repository.GuiaProcesadaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConsumidorService {

    private final RabbitTemplate rabbitTemplate;
    private final GuiaProcesadaRepository guiaProcesadaRepository;
    private final ProductorService productorService;

    @Value("${rabbitmq.cola.guias}")
    private String colaGuias;

    public ConsumidorService(
            RabbitTemplate rabbitTemplate,
            GuiaProcesadaRepository guiaProcesadaRepository,
            ProductorService productorService) {

        this.rabbitTemplate = rabbitTemplate;
        this.guiaProcesadaRepository = guiaProcesadaRepository;
        this.productorService = productorService;
    }

    public GuiaProcesada consumirGuia() {

        Object mensaje = rabbitTemplate.receiveAndConvert(colaGuias);

        if (mensaje == null) {
            return null;
        }

        if (!(mensaje instanceof GuiaDespacho guia)) {
            throw new IllegalStateException(
                    "El mensaje recibido no corresponde a una guía de despacho"
            );
        }

        try {
            GuiaProcesada procesada = new GuiaProcesada();

            procesada.setNumeroGuia(guia.getNumeroGuia());
            procesada.setCliente(guia.getCliente());
            procesada.setDireccionDestino(guia.getDireccionDestino());
            procesada.setFecha(guia.getFecha());
            procesada.setTransportista(guia.getTransportista());
            procesada.setRutaEfs(guia.getRutaEfs());
            procesada.setRutaS3(guia.getRutaS3());

            GuiaProcesada guardada =
                    guiaProcesadaRepository.save(procesada);

            System.out.println(
                    "Guía consumida y almacenada: "
                            + guardada.getNumeroGuia()
            );

            return guardada;

        } catch (Exception exception) {

            productorService.enviarError(
                    guia,
                    exception.getMessage()
            );

            throw new IllegalStateException(
                    "No fue posible procesar la guía; se envió a la cola de errores",
                    exception
            );
        }
    }
}