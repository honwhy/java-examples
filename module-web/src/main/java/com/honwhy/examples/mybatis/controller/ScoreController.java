package com.honwhy.examples.mybatis.controller;

import com.honwhy.examples.mybatis.entity.Score;
import com.honwhy.examples.mybatis.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{studentId}")
    public List<Score> getScoresByStudentId(@PathVariable Long studentId) {
        return scoreService.getScoresByStudentId(studentId);
    }
}
