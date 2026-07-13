package com.duoc.guias.controller;

import com.duoc.guias.model.GuiaDespacho;
import com.duoc.guias.service.ProductorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rabbit")
public class ProductorController {

    private final ProductorService productorService;

    public ProductorController(ProductorService productorService) {
        this.productorService = productorService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarGuia(
            @RequestBody GuiaDespacho guia) {

        productorService.enviarGuia(guia);

        return ResponseEntity.accepted()
                .body("Guía enviada correctamente a RabbitMQ");
    }
}