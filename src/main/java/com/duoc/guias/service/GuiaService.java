package com.duoc.guias.service;

import com.duoc.guias.model.GuiaDespacho;
import com.duoc.guias.repository.GuiaRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Service
public class GuiaService {

    private final GuiaRepository repository;
    private final AwsS3Service awsS3Service;
    private final ProductorService productorService;

    @Value("${efs.ruta}")
    private String rutaEfs;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public GuiaService(
            GuiaRepository repository,
            AwsS3Service awsS3Service,
            ProductorService productorService) {
        this.repository = repository;
        this.awsS3Service = awsS3Service;
        this.productorService = productorService;
    }

    private String generarNumeroGuia(Long id) {
        return String.format("G%05d", id);
    }

    private void crearPDF(File archivo, GuiaDespacho guia) throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("GUIA DE DESPACHO");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 720);
        contentStream.showText("Numero: " + guia.getNumeroGuia());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 700);
        contentStream.showText("Transportista: " + guia.getTransportista());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 680);
        contentStream.showText("Cliente: " + guia.getCliente());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 660);
        contentStream.showText("Direccion destino: " + guia.getDireccionDestino());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 640);
        contentStream.showText("Fecha: " + guia.getFecha().toString());
        contentStream.endText();

        contentStream.close();
        document.save(archivo);
        document.close();
    }

    public GuiaDespacho guardar(GuiaDespacho guia) {
        try {
            guia.setFecha(LocalDate.now());

            // Guardar primero para obtener el ID generado por la base de datos
            GuiaDespacho guiaGuardada = repository.save(guia);

            // Generar número de guía basado en el ID de la base de datos
            String numeroGuia = generarNumeroGuia(guiaGuardada.getId());
            guiaGuardada.setNumeroGuia(numeroGuia);

            File carpeta = new File(rutaEfs);
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            String nombreArchivo = numeroGuia + ".pdf";
            File archivo = new File(carpeta, nombreArchivo);

            crearPDF(archivo, guiaGuardada);

            guiaGuardada.setRutaEfs(archivo.getPath());

            String keyS3 = guiaGuardada.getFecha() + "/" + guiaGuardada.getTransportista().replace(" ", "_") + "/" + nombreArchivo;

            awsS3Service.subirArchivo(bucket, keyS3, archivo);

            guiaGuardada.setRutaS3(keyS3);

            GuiaDespacho guiaFinal = repository.save(guiaGuardada);
            productorService.enviarGuia(guiaFinal);

            return guiaFinal;

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

        crearPDF(archivo, guia);

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
