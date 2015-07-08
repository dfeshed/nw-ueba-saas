package com.rsa.asoc.sa.ui.investigation.web.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to return the currently authenticated user.
 *
 * @author athielke
 * @since 11.0.0.0
 */
@RestController
public class UserController {

    @RequestMapping("/api/user")
    public Object user(Authentication authentication) {
        return authentication.getPrincipal();
    }
}
