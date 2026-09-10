package com.seriesapp;

import com.seriesapp.entity.Comment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class SeriesAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeriesAppApplication.class, args);

    }
}
