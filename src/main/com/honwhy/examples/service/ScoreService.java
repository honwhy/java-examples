package com.honwhy.examples.service;


import com.honwhy.examples.entity.Score;

import java.util.List;

public interface ScoreService {
    List<Score> getScoresByStudentId(Long studentId);
}