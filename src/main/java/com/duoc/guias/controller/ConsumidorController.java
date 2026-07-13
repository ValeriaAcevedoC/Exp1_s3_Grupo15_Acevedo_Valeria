package com.duoc.guias.controller;

import com.duoc.guias.model.GuiaProcesada;
import com.duoc.guias.service.ConsumidorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rabbit")
public class ConsumidorController {

    private final ConsumidorService consumidorService;

    public ConsumidorController(
            ConsumidorService consumidorService) {
        this.consumidorService = consumidorService;
    }

    @PostMapping("/consumir")
    public ResponseEntity<?> consumirGuia() {

        GuiaProcesada guia =
                consumidorService.consumirGuia();

        if (guia == null) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity.ok(guia);
    }
}