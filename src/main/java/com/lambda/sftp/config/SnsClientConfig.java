package com.lambda.sftp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SnsClientConfig {
    
    @Value("${aws.region:}")
    private String region;

    @Bean
    public SnsClient snsClient(){
        return SnsClient.builder().region(Region.of(region)).build();
    }

}
