package com.lambda.sftp.lambda;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.lambda.sftp.SftpApplication;
import com.lambda.sftp.config.SnsClientConfig;
import com.lambda.sftp.service.CsvFileProcessor;
import com.lambda.sftp.service.S3util;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
public class LambdaHandler implements RequestHandler<Map<String, Object>, String> {

    private static final ApplicationContext context;
    private static final S3util s3util;
    private static final CsvFileProcessor csvFileProcessor;
    private static final SnsClientConfig snsClient;

    @Value("${aws.sns.topic:}")
    private String snsTopic;    


    static {
        // Bootstrap Spring Application Context once when class is loaded
        context = SpringApplication.run(SftpApplication.class);
        s3util = context.getBean(S3util.class);
        csvFileProcessor = context.getBean(CsvFileProcessor.class);
        snsClient=context.getBean(SnsClientConfig.class);
    }

    // Required public no-argument constructor for AWS Lambda runtime
    public LambdaHandler() {}

    @Override
    public String handleRequest(Map<String, Object> json, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Lambda event triggered!");
        try {
            List<Map<String, Object>> records = (List<Map<String, Object>>) json.get("Records");
            if (records == null || records.isEmpty()) {
                return "Failed to process: Records array is missing or empty";
            }

            Map<String, Object> firstRecord = records.get(0);
            Map<String, Object> s3 = (Map<String, Object>) firstRecord.get("s3");
            if (s3 == null) {
                return "Failed to process: S3 block is missing in the event record";
            }

            Map<String, Object> bucketDetails = (Map<String, Object>) s3.get("bucket");
            String bucketName = bucketDetails.get("name").toString();

            Map<String, Object> objDetails = (Map<String, Object>) s3.get("object");
            String rawKey = objDetails.get("key").toString();
            String objectName = rawKey;

            if (bucketName == null || objectName == null) {
                return "Failed to process: bucketName or key is missing from payload.";
            }

            logger.log("Received object with key=" + objectName + " from the bucket " + bucketName);
            
            ResponseInputStream<GetObjectResponse> stream = s3util.downloadFile(bucketName, objectName);
            String contentType = stream.response().contentType();
            MultipartFile file = new MockMultipartFile("file", objectName, contentType, stream);
            boolean result = csvFileProcessor.processCsv(file);
           
            String message = "Hi,\n\nThis is to inform you that the file you uploaded is "+ (result ? "Successfully processed! with file name: "+objectName : "Failed to process with file name: "+objectName)+"\n\nBest regards,\nSFTP Automation";
           
           
            PublishRequest request = PublishRequest.builder()
                      .subject("Sftp File Status Acme Company.")
                      .message(message)
                      .topicArn(snsTopic)
                      .build();

            snsClient.snsClient().publish(request);
            return (result ? "Successfully processed! with file name: "+objectName : "Failed to process with file name: "+objectName);
        } catch (Exception e) {
            logger.log("Error occurred in lambda handler, msg={},erroor={}" + e.getMessage()+e);
            return "Failed to process csv file with exception";
        }
    }
}
