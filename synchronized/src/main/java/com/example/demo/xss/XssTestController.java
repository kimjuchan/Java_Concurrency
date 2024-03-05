package com.example.demo.xss;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/xss")
public class XssTestController {

    @PostMapping("/list")
    public ResponseEntity<XssRequestDto> xssTest(@RequestBody XssRequestDto xssRequestDto){
        log.info("###xss  처리");
        log.info("###xss 적용 결과 : " + xssRequestDto.getContent());


        return new ResponseEntity<XssRequestDto>(xssRequestDto,HttpStatus.OK);

    }


}
