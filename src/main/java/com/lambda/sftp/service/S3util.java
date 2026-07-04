package com.lambda.sftp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Component
public class S3util {
    
    @Value("${aws.bucket.name:}")
    private String bucketName;

    private final S3Client s3Client;

    public S3util(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public ResponseInputStream<GetObjectResponse> downloadFile(String bucketName, String key){
        GetObjectRequest request = GetObjectRequest.builder()
                                    .bucket(bucketName).key(key).build();
        return s3Client.getObject(request);
    }
}
