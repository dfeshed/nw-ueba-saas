package com.rsa.asoc.sa.ui.investigation.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A test controller for the proof of concept.
 *
 * @author nguyek7
 * @since 10.6.0.0
 */
@RestController
public class HelloController {

    @RequestMapping(value = {"/"})
    public String hello() {
        return "Hello World!";
    }
}
