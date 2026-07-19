package com.duoc.guiasbff.controller;

import com.duoc.guias.model.GuiaDespacho;
import com.duoc.guiasbff.service.GuiaPublisherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/bff/guias")
public class GuiaBffController {

    private final GuiaPublisherService guiaPublisherService;

    public GuiaBffController(GuiaPublisherService guiaPublisherService) {
        this.guiaPublisherService = guiaPublisherService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> enviarGuia(
            @Valid @RequestBody GuiaDespacho guia) {

        guiaPublisherService.enviarGuia(guia);

        return ResponseEntity.accepted()
                .body(Map.of(
                        "estado", "ACEPTADA",
                        "mensaje", "Guia recibida por el BFF y enviada a la cola de creacion"
                ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "servicio", "guias-bff",
                "estado", "OK"
        ));
    }
}
