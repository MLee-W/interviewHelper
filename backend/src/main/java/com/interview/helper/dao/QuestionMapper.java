package com.interview.helper.dao;

import com.interview.helper.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QuestionMapper {
    int batchInsert(@Param("questions") List<Question> questions);
    List<Question> searchQuestions(@Param("keywords") List<String> keywords);
    List<Question> findAll(@Param("offset") int offset, @Param("pageSize") int pageSize);
    int count();
} 