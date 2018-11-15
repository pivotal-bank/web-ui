package io.pivotal.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InitOAuth2Authentication {

    @GetMapping("/oauth-login")
    public String redirectOnSuccess() {
        return "redirect:/?message=Logged In!";
    }

}
