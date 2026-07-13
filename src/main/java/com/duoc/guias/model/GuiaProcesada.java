package com.duoc.guias.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "guias_procesadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuiaProcesada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroGuia;

    private String cliente;

    private String direccionDestino;

    private LocalDate fecha;

    private String transportista;

    private String rutaEfs;

    private String rutaS3;
}