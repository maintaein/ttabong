package com.ttabong.health;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("health")
@RestController
public class Healthcheck {

    @RequestMapping("")
    public HttpStatus healthCheck() {
        return HttpStatus.OK;
    }
}
