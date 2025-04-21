package com.honwhy.examples.copy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/copy")
public class CopyController {

    @PostMapping("/callback")
    public String callback(@RequestBody Map<String, Object> params) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // 转换为JSON字符串
        String json = mapper.writeValueAsString(params);
        System.out.println("callback: " + json);
        return json;
    }
}
