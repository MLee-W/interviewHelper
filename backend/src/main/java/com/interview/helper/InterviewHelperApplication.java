package com.interview.helper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.interview.helper.dao")
public class InterviewHelperApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterviewHelperApplication.class, args);
    }
} 