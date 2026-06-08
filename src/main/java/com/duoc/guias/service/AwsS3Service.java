package com.duoc.guias.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.File;

@Service
public class AwsS3Service {

    private final S3Client s3Client;

    public AwsS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void subirArchivo(String bucket, String key, File archivo) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(request, RequestBody.fromFile(archivo));
    }

    public byte[] descargarArchivo(String bucket, String key) {

    GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        ResponseBytes<GetObjectResponse> objeto =
            s3Client.getObjectAsBytes(request);

            return objeto.asByteArray();
    }

    public void eliminarArchivo(String bucket, String key) {
    DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        s3Client.deleteObject(request);
    }
}