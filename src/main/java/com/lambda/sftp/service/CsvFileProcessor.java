package com.lambda.sftp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lambda.sftp.entity.Users;
import com.lambda.sftp.repo.UsersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class CsvFileProcessor{

    private final UsersRepository usersRepository;

    public boolean processCsv (MultipartFile file) {

        List<Users> users = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            boolean isHeader = true;
            String line;
            log.info("Started processing CSV file");
            while ((line=br.readLine())!=null) {
                if(isHeader){
                    isHeader=false;
                    continue;
                }
                String data[]=line.split(",",-1);
                if(data.length>0 && data.length>=4){
                    Users user = new Users();
                    user.setEmail(data[0]);
                    user.setName(data[1]);
                    user.setPhone(data[2]);
                    user.setGender(data[3]);
                    users.add(user);
                }
            }
            //save users to db.
            List<Users> savedUsers = usersRepository.saveAll(users);
            return (!savedUsers.isEmpty()||savedUsers!=null);
        } catch (IOException e) {
            log.error("Error occured while processing csv file, msg={},error={}",e.getMessage(),e.getStackTrace());
            return false;
        }
        
    }
}