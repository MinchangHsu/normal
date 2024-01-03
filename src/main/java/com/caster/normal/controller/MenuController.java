package com.caster.normal.controller;

import com.caster.normal.entity.Menu;
import com.caster.normal.mapper.MenuMapper;
import com.caster.normal.model.InsertOrUpdateMenuReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        log.info("findAll >>> time:{}, reqThreadName:{}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),reqThreadName);
        return ResponseEntity.ok(mapper.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id){
        log.info("findById >>> time:{}, currentThread:{}, reqId:{}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")), Thread.currentThread().getName(), id);
        return ResponseEntity.ok(mapper.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateById(@PathVariable Long id, @RequestBody InsertOrUpdateMenuReq req){
        log.info("updateById >>> time:{}, currentThread:{}, reqId:{}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")), Thread.currentThread().getName(), id);
        Menu updateInfo = new Menu().setId(id);
        BeanUtils.copyProperties(req, updateInfo, "id");
        return ResponseEntity.ok(mapper.updateById(updateInfo));
    }

    @PostMapping("")
    public ResponseEntity insert(@RequestBody InsertOrUpdateMenuReq req){
        log.info("updateById >>> time:{}, currentThread:{}, reqId:{}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")), Thread.currentThread().getName());
        Menu updateInfo = new Menu();
        BeanUtils.copyProperties(req, updateInfo);
        mapper.insert(updateInfo);
        return ResponseEntity.ok(updateInfo);
    }
}
