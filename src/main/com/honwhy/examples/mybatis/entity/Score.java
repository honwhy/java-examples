package com.honwhy.examples.mybatis.entity;

import lombok.Data;

@Data
public class Score {
    private Long id;
    private Long studentId;
    private String courseName;
    private Integer score;
}