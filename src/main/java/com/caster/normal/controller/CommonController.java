package com.caster.normal.controller;

import com.caster.normal.mapper.MenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author caster.hsu
 * @Since 2023/12/26
 */
@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {
    @Value("${customer.appVersion:V0}")
    String appVersion;

    @Autowired
    MenuMapper mapper;
    @GetMapping("/healthy")
    public ResponseEntity healthyCheck(HttpServletRequest req){
        // init CP checker:readinessProbe
        String checkerName = req.getHeader("checker");
        StringBuilder sb = new StringBuilder();
        sb.append(System.getenv("POD_ID"));
        sb.append(" >> ");
        sb.append(System.getenv("POD_IP"));
        sb.append(" >> ");
        sb.append(appVersion);
        log.debug("checker:{} > appVersion:{}, time:{}", StringUtils.rightPad(checkerName, 15, " "), appVersion, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.ok(sb.toString());
    }

    @GetMapping("/time")
    public ResponseEntity timeCheck(){
        return ResponseEntity.ok(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @GetMapping("/preStop")
    public ResponseEntity preStop(){
        log.debug("got preStop action.......");
        return ResponseEntity.ok(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
