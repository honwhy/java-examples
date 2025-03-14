package com.honwhy.examples.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honwhy.examples.mybatis.entity.Score;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
    List<Score> getScoresByStudentId(Long studentId);
}
