package com.honwhy.examples.service.impl;

import com.honwhy.examples.entity.Score;
import com.honwhy.examples.mapper.ScoreMapper;
import com.honwhy.examples.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public List<Score> getScoresByStudentId(Long studentId) {
        return scoreMapper.getScoresByStudentId(studentId);
    }
}
