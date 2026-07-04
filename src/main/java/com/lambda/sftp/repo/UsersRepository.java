package com.lambda.sftp.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lambda.sftp.entity.Users;

public interface  UsersRepository extends JpaRepository<Users,UUID> {
    
}
