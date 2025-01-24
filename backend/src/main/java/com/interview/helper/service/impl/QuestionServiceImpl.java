package com.interview.helper.service.impl;

import com.interview.helper.dao.QuestionMapper;
import com.interview.helper.entity.Question;
import com.interview.helper.model.PageResult;
import com.interview.helper.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final DocParserService docParserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveQuestions(List<Question> questions) {
        if (questions != null && !questions.isEmpty()) {
            questionMapper.batchInsert(questions);
        }
    }

    @Override
    public List<Question> searchQuestions(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return questionMapper.findAll(0, 10);  // 默认返回前10条
        }
        
        // 获取所有题目
        List<Question> allQuestions = questionMapper.searchQuestions(Collections.emptyList());
        
        // 将关键字分词
        Set<String> keywords = new HashSet<>();
        StringBuilder tempWord = new StringBuilder();
        
        for (char ch : keyword.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                // 遇到空格，保存当前英文单词
                if (tempWord.length() > 0) {
                    keywords.add(tempWord.toString());
                    tempWord = new StringBuilder();
                }
            } else if (isChinese(ch)) {
                // 如果是中文字符，先保存之前的英文单词（如果有）
                if (tempWord.length() > 0) {
                    keywords.add(tempWord.toString());
                    tempWord = new StringBuilder();
                }
                // 将中文字符作为单独的关键字
                keywords.add(String.valueOf(ch));
            } else {
                // 英文和数字等字符累加
                tempWord.append(ch);
            }
        }
        
        // 处理最后一个英文单词
        if (tempWord.length() > 0) {
            keywords.add(tempWord.toString());
        }
        
        log.debug("分词结果: {}", keywords);
        
        // 计算每个题目的匹配度
        return allQuestions.stream()
                .map(q -> {
                    String title = q.getTitle();
                    int matchScore = 0;
                    
                    // 计算每个关键字的匹配分数
                    for (String kw : keywords) {
                        if (isChinese(kw.charAt(0))) {
                            // 中文关键字完全匹配得3分，部分匹配得1分
                            if (title.contains(kw)) {
                                matchScore += 3;
                            }
                        } else {
                            // 英文关键字，忽略大小写
                            String titleLower = title.toLowerCase();
                            String kwLower = kw.toLowerCase();
                            // 完全匹配得3分，作为单词边界匹配得2分，部分匹配得1分
                            if (titleLower.matches(".*\\b" + kwLower + "\\b.*")) {
                                matchScore += 3;  // 作为完整单词匹配
                            } else if (titleLower.contains(kwLower)) {
                                matchScore += 1;  // 部分匹配
                            }
                        }
                    }
                    
                    q.setMatchScore(matchScore);
                    return q;
                })
                .filter(q -> q.getMatchScore() > 0)  // 只保留有匹配的结果
                .sorted(Comparator.comparing(Question::getMatchScore).reversed())  // 按匹配分数降序
                .limit(10)  // 只取前10条
                .collect(Collectors.toList());
    }

    /**
     * 判断字符是否是中文
     */
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
    }

    @Override
    public PageResult<Question> findAll(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Question> list = questionMapper.findAll(offset, pageSize);
        int total = questionMapper.count();
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Question> parseAndSaveDocument(MultipartFile file) {
        List<Question> questions = docParserService.parseDocument(file);
        batchSaveQuestions(questions);
        return questions;
    }
} 