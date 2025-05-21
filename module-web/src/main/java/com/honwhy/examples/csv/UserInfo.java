package com.honwhy.examples.csv;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {

    @TkHeader(name = "名称")
    private String name;
    @TkHeader(name = "生日", pattern = "yyyy-MM-dd")
    private Date birthday;
}
