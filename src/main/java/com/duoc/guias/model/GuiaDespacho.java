package com.duoc.guias.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroGuia;

    private String transportista;

    private String cliente;

    private String direccionDestino;

    private LocalDate fecha;

    private String rutaEfs;

    private String rutaS3;
}