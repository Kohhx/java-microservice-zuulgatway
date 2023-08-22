package com.kohhx.jobservice.controller;

import com.kohhx.jobservice.DTO.MessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@RestController
//@RequestMapping("/job")
public class JobController {

    @GetMapping("/get")
    public ResponseEntity<MessageDTO> getJob(@RequestHeader Map<String,String> headers) {
        headers.forEach((key,value) ->{
            System.out.println("Header Name: "+key+" Header Value: "+value);
        });
        return new ResponseEntity<>(new MessageDTO("Got all jobs"), HttpStatus.OK);
    }
}
