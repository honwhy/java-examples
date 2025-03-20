package com.honwhy.examples.copy.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/copy")
public class CopyController {

    @PostMapping("/callback")
    public String callback() {
        return "copy";
    }
}
