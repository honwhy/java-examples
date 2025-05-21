package com.honwhy.examples.csv;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class CsvDownloadController {

    @GetMapping("/download/userinfo")
    public void downloadCsv(HttpServletResponse response) throws Exception {
        List<UserInfo> users = new ArrayList<>();

        // 模拟数据
        UserInfo user = new UserInfo();
        user.setName("张三");
        user.setBirthday(new Date());
        users.add(user);

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"userinfo.csv\"");

        CsvExportUtil.writeCsv(users, response.getOutputStream());
    }
}

