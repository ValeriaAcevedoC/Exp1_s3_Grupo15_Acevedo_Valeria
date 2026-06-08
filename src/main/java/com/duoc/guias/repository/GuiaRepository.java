package com.duoc.guias.repository;

import com.duoc.guias.model.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GuiaRepository extends JpaRepository<GuiaDespacho, Long> {
    List<GuiaDespacho> findByTransportistaAndFecha(String transportista, LocalDate fecha);
}