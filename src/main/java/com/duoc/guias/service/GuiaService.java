package com.duoc.guias.service;

import com.duoc.guias.model.GuiaDespacho;
import com.duoc.guias.repository.GuiaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

@Service
public class GuiaService {

    private final GuiaRepository repository;
    private final AwsS3Service awsS3Service;

    @Value("${efs.ruta}")
    private String rutaEfs;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public GuiaService(GuiaRepository repository, AwsS3Service awsS3Service) {
        this.repository = repository;
        this.awsS3Service = awsS3Service;
    }

    public GuiaDespacho guardar(GuiaDespacho guia) {
        try {
            guia.setFecha(LocalDate.now());

            File carpeta = new File(rutaEfs);
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            String nombreArchivo = guia.getNumeroGuia() + ".txt";
            File archivo = new File(carpeta, nombreArchivo);

            FileWriter writer = new FileWriter(archivo);
            writer.write("GUIA DE DESPACHO\n");
            writer.write("Numero: " + guia.getNumeroGuia() + "\n");
            writer.write("Transportista: " + guia.getTransportista() + "\n");
            writer.write("Cliente: " + guia.getCliente() + "\n");
            writer.write("Direccion destino: " + guia.getDireccionDestino() + "\n");
            writer.write("Fecha: " + guia.getFecha() + "\n");
            writer.close();

            guia.setRutaEfs(archivo.getPath());

            String keyS3 = guia.getFecha() + "/" + guia.getTransportista().replace(" ", "_") + "/" + nombreArchivo;

            awsS3Service.subirArchivo(bucket, keyS3, archivo);

            guia.setRutaS3(keyS3);

            return repository.save(guia);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear guia y subir a S3: " + e.getMessage());
        }
    }

    public List<GuiaDespacho> listarTodas() {
        return repository.findAll();
    }

    public GuiaDespacho buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public byte[] descargarArchivo(Long id) {

        GuiaDespacho guia = repository.findById(id)
            .orElseThrow();

        return awsS3Service.descargarArchivo(bucket, guia.getRutaS3());
    }

    public GuiaDespacho actualizarGuia(Long id, GuiaDespacho datosNuevos) {
      try {
        GuiaDespacho guia = repository.findById(id).orElseThrow();

        guia.setTransportista(datosNuevos.getTransportista());
        guia.setCliente(datosNuevos.getCliente());
        guia.setDireccionDestino(datosNuevos.getDireccionDestino());
        guia.setFecha(LocalDate.now());

        File archivo = new File(guia.getRutaEfs());

        FileWriter writer = new FileWriter(archivo);
        writer.write("GUIA DE DESPACHO ACTUALIZADA\n");
        writer.write("Numero: " + guia.getNumeroGuia() + "\n");
        writer.write("Transportista: " + guia.getTransportista() + "\n");
        writer.write("Cliente: " + guia.getCliente() + "\n");
        writer.write("Direccion destino: " + guia.getDireccionDestino() + "\n");
        writer.write("Fecha: " + guia.getFecha() + "\n");
        writer.close();

        awsS3Service.subirArchivo(bucket, guia.getRutaS3(), archivo);

        return repository.save(guia);

        } catch (Exception e) {
        throw new RuntimeException("Error al actualizar guia en S3: " + e.getMessage());
        }
    }
    public void eliminarGuia(Long id) {
        try {
            GuiaDespacho guia = repository.findById(id).orElseThrow();

            awsS3Service.eliminarArchivo(bucket, guia.getRutaS3());

            File archivoLocal = new File(guia.getRutaEfs());
            if (archivoLocal.exists()) {
            archivoLocal.delete();
            }

            repository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar guia: " + e.getMessage());
        }
    }

    public List<GuiaDespacho> buscarPorTransportistaYFecha(String transportista, LocalDate fecha) {
        return repository.findByTransportistaAndFecha(transportista, fecha);
    }
}