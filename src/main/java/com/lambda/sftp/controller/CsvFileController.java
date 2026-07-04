package com.lambda.sftp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lambda.sftp.service.CsvFileProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/csv")
public class CsvFileController {
    
    private final CsvFileProcessor csvFileProcessor;

    @PostMapping("/upload")
    public ResponseEntity<String> processCsvFile(@RequestBody MultipartFile file){

        boolean result = csvFileProcessor.processCsv(file);
        return ResponseEntity.ok(result?"Csv file processed successfully" : "Failed to process csv file.");

    }
    
}
