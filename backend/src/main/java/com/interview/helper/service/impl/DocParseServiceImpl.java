package com.interview.helper.service.impl;

import com.interview.helper.entity.Question;
import com.interview.helper.service.DocParseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DocParseServiceImpl implements DocParseService {
    
    private static final int TITLE_FONT_SIZE = 16;
    private static final int QUESTION_FONT_SIZE = 14;

    @Override
    public List<Question> parseDocument(MultipartFile file) {
        List<Question> questions = new ArrayList<>();
        try {
            XWPFDocument document = new XWPFDocument(file.getInputStream());
            Question currentQuestion = null;
            StringBuilder contentBuilder = new StringBuilder();

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                // 获取段落的样式
                String text = paragraph.getText().trim();
                if (text.isEmpty()) {
                    continue;
                }

                // 获取段落的字体大小
                int fontSize = getFontSize(paragraph);

                // 如果是问题标题（二级标题）
                if (fontSize == QUESTION_FONT_SIZE) {
                    // 保存上一个问题
                    if (currentQuestion != null && contentBuilder.length() > 0) {
                        currentQuestion.setContent(contentBuilder.toString().trim());
                        questions.add(currentQuestion);
                        contentBuilder = new StringBuilder();
                    }
                    
                    // 创建新的问题
                    currentQuestion = new Question();
                    currentQuestion.setTitle(text);
                }
                // 如果是内容
                else if (fontSize < QUESTION_FONT_SIZE && currentQuestion != null) {
                    contentBuilder.append(text).append("\n");
                }
                // 忽略大标题
            }

            // 保存最后一个问题
            if (currentQuestion != null && contentBuilder.length() > 0) {
                currentQuestion.setContent(contentBuilder.toString().trim());
                questions.add(currentQuestion);
            }

        } catch (IOException e) {
            log.error("解析文档失败", e);
            throw new RuntimeException("文档解析失败：" + e.getMessage());
        }
        return questions;
    }

    /**
     * 获取段落的字体大小
     */
    private int getFontSize(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs != null && !runs.isEmpty()) {
            XWPFRun run = runs.get(0);
            return run.getFontSize();
        }
        return 0;
    }
} 