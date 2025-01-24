package com.interview.helper.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.interview.helper.entity.Question;

public interface DocParseService {
    /**
     * 解析文档并返回问题列表
     *
     * @param file 上传的文档文件
     * @return 解析出的问题列表
     */
    List<Question> parseDocument(MultipartFile file);
} 