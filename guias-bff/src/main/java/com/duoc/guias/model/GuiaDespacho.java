package com.duoc.guias.model;

import jakarta.validation.constraints.NotBlank;
public class GuiaDespacho {

    @NotBlank
    private String transportista;

    @NotBlank
    private String cliente;

    @NotBlank
    private String direccionDestino;

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }
}
