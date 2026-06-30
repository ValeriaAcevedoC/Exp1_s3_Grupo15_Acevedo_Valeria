package com.duoc.guias.controller;

import com.duoc.guias.model.GuiaDespacho;
import com.duoc.guias.service.GuiaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {

    private final GuiaService service;

    public GuiaController(GuiaService service) {
        this.service = service;
    }

    @PostMapping
    public GuiaDespacho crearGuia(@RequestBody GuiaDespacho guiaData) {
        return service.guardar(guiaData);
    }

    @GetMapping
    public List<GuiaDespacho> listarGuias() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public GuiaDespacho buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {

        byte[] archivo = service.descargarArchivo(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guia-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }

    @PutMapping("/{id}")
    public GuiaDespacho actualizarGuia(@PathVariable Long id, @RequestBody GuiaDespacho datosNuevos) {
        return service.actualizarGuia(id, datosNuevos);
    }

    @DeleteMapping("/{id}")
    public String eliminarGuia(@PathVariable Long id) {
        service.eliminarGuia(id);
        return "Guia eliminada correctamente";
    }

    @GetMapping("/buscar")
    public List<GuiaDespacho> buscarPorTransportistaYFecha(
        @RequestParam String transportista,
        @RequestParam String fecha) {

        LocalDate fechaConvertida = LocalDate.parse(fecha);

        return service.buscarPorTransportistaYFecha(transportista, fechaConvertida);
    }
}