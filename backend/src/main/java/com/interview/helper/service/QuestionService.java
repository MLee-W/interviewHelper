package com.interview.helper.service;

import com.interview.helper.entity.Question;
import com.interview.helper.model.PageResult;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface QuestionService {
    void batchSaveQuestions(List<Question> questions);
    List<Question> searchQuestions(String keyword);
    List<Question> parseAndSaveDocument(MultipartFile file);
    PageResult<Question> findAll(int pageNum, int pageSize);
} 