package com.honwhy.examples.mybatis.service.impl;

import com.honwhy.examples.mybatis.entity.Score;
import com.honwhy.examples.mybatis.mapper.ScoreMapper;
import com.honwhy.examples.mybatis.service.ScoreService;
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
