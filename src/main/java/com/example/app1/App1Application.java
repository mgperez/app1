package com.example.app1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class App1Application {

    public static void main(String[] args) {
        SpringApplication.run(App1Application.class, args);
        log.info("testing logging with lombok");
    }

}

