package com.interview.helper.controller;

import com.interview.helper.entity.Question;
import com.interview.helper.model.PageResult;
import com.interview.helper.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestions(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(questionService.searchQuestions(keyword));
    }

    @GetMapping("/list")
    public ResponseEntity<PageResult<Question>> getQuestions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(questionService.findAll(pageNum, pageSize));
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Question>> uploadQuestions(@RequestParam("file") MultipartFile file) {
        List<Question> questions = questionService.parseAndSaveDocument(file);
        return ResponseEntity.ok(questions);
    }
} 