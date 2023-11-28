package com.caster.normal.controller;

import com.caster.normal.mapper.MenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author caster.hsu
 * @Since 2023/9/14
 */
@RestController
@Slf4j
public class MenuController {
    @Autowired
    MenuMapper mapper;

    @GetMapping("/test")
    public ResponseEntity test(){
        return ResponseEntity.ok("Hello World.");
    }
    @GetMapping("/list")
    public ResponseEntity findAll(@RequestParam("threadName") String reqThreadName){
        log.info("time:{}, reqThreadName:{}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),reqThreadName);
        return ResponseEntity.ok(mapper.findAll());
    }

    @GetMapping("/a")
    public ResponseEntity A(){
        return ResponseEntity.ok("Hello World >>> a.");
    }

    @GetMapping("/b")
    public ResponseEntity B(){
        return ResponseEntity.ok("Hello World >>> b.");
    }

    @GetMapping("/add/{type}")
    public ResponseEntity add(@PathVariable String type){
        return ResponseEntity.ok("Add success.");
    }
}
