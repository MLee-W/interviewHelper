package com.interview.helper.controller;

import com.interview.helper.entity.Question;
import com.interview.helper.service.DocParseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocParseService docParseService;

    @PostMapping("/upload")
    public ResponseEntity<List<Question>> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".doc") && !fileName.endsWith(".docx"))) {
            return ResponseEntity.badRequest().build();
        }

        List<Question> questions = docParseService.parseDocument(file);
        return ResponseEntity.ok(questions);
    }
} 