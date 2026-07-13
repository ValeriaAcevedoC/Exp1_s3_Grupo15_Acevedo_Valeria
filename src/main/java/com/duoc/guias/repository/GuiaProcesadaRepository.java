package com.duoc.guias.repository;

import com.duoc.guias.model.GuiaProcesada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuiaProcesadaRepository
        extends JpaRepository<GuiaProcesada, Long> {
}