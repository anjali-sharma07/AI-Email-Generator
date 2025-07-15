package com.email.generator.controller;

import com.email.generator.entity.EmailRequest;
import com.email.generator.service.EmailGenerateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailGenerateController {


    private final EmailGenerateService service;


    @PostMapping("/generate")
    public ResponseEntity<String> email(@RequestBody EmailRequest request){
       String response =  service.getEmail(request);
        return ResponseEntity.ok(response);

    }
}
