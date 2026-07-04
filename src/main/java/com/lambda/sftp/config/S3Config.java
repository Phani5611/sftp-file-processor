package com.lambda.sftp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@Slf4j
public class S3Config{

    @Value("${aws.region:}")
    private String region;

    @Value("${aws.endpoint-url:}")
    private String endpointUrl;

    @Value("${aws.credentials.access-key:}")
    private String accessKey;

    @Value("${aws.credentials.secret-key:}")
    private String secretKey;

    @Bean
    public S3Client s3Client(){
        log.info("AWS endpoint url:{}",endpointUrl);
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region))
                .crossRegionAccessEnabled(true);
               
        if (endpointUrl != null && !endpointUrl.isBlank()) {
            builder.endpointOverride(URI.create(endpointUrl))
            .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
        .pathStyleAccessEnabled(true).build());
        }
       
        return builder.build();
    }

    @Bean     
    public S3Presigner s3Presigner(){
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(region));
                

        if (endpointUrl != null && !endpointUrl.isBlank()) {
            builder.endpointOverride(URI.create(endpointUrl))
            .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ));;
        }

        return builder.build();
    }
}