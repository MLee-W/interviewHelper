package com.interview.helper.service.impl;

import com.interview.helper.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DocParserService {
    
    private static final int TITLE_FONT_SIZE = 16;
    private static final int QUESTION_FONT_SIZE = 14;

    public List<Question> parseDocument(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".doc") && !fileName.endsWith(".docx"))) {
            throw new IllegalArgumentException("只支持.doc或.docx格式的文件");
        }

        try {
            XWPFDocument document = new XWPFDocument(file.getInputStream());
            return extractQuestions(document);
        } catch (IOException e) {
            log.error("解析文档失败", e);
            throw new RuntimeException("文档解析失败：" + e.getMessage());
        }
    }

    private List<Question> extractQuestions(XWPFDocument document) {
        List<Question> questions = new ArrayList<>();
        Question currentQuestion = null;
        StringBuilder contentBuilder = new StringBuilder();
        boolean isFirstQuestion = true;

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText().trim();
            if (text.isEmpty()) {
                continue;
            }

            // 获取段落的字体大小
            int fontSize = getParagraphFontSize(paragraph);
            log.debug("段落文本: {}, 字体大小: {}", text, fontSize);  // 添加日志

            // 如果是问题标题（二级标题）
            if (fontSize >= QUESTION_FONT_SIZE) {
                // 保存上一个问题
                if (currentQuestion != null && contentBuilder.length() > 0) {
                    String content = contentBuilder.toString().trim();
                    // 检查内容长度
                    if (content.length() > 65535) {  // text类型的最大长度
                        log.warn("问题内容超长，长度：{}，标题：{}", content.length(), currentQuestion.getTitle());
                        content = content.substring(0, 65535);  // 截断内容
                    }
                    currentQuestion.setContent(content);
                    questions.add(currentQuestion);
                }
                
                // 创建新的问题
                currentQuestion = new Question();
                currentQuestion.setTitle(text);
                contentBuilder = new StringBuilder();
                isFirstQuestion = false;
            }
            // 如果是内容
            else if (!isFirstQuestion) {  // 忽略第一个问题之前的内容
                contentBuilder.append(text).append("\n");
            }
        }

        // 保存最后一个问题
        if (currentQuestion != null && contentBuilder.length() > 0) {
            String content = contentBuilder.toString().trim();
            // 检查内容长度
            if (content.length() > 65535) {
                log.warn("问题内容超长，长度：{}，标题：{}", content.length(), currentQuestion.getTitle());
                content = content.substring(0, 65535);
            }
            currentQuestion.setContent(content);
            questions.add(currentQuestion);
        }

        return questions;
    }

    private int getParagraphFontSize(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (!runs.isEmpty()) {
            for (XWPFRun run : runs) {
                if (run.getFontSize() != -1) {  // -1 表示未设置字体大小
                    return run.getFontSize();
                }
            }
        }
        return 0;  // 默认字体大小
    }
} 