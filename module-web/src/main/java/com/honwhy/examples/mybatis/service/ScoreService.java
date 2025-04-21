package com.honwhy.examples.mybatis.service;


import com.honwhy.examples.mybatis.entity.Score;

import java.util.List;

public interface ScoreService {
    List<Score> getScoresByStudentId(Long studentId);
}