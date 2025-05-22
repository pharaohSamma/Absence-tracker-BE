package com.example.studentabsencetracker.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Basic controller works!";
    }

    @GetMapping("/api/test")
    public String apiTest() {
        return "API test works!";
    }
}