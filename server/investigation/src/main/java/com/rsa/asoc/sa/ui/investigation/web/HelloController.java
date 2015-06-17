package com.rsa.asoc.sa.ui.investigation.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = {"/"})
    public String hello() {
        return "Hello World!";
    }
}
