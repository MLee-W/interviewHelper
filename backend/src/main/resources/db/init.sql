-- 创建数据库
CREATE DATABASE IF NOT EXISTS interview_qa_helper
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

-- 使用数据库
USE interview_qa_helper;

-- 创建问题表
CREATE TABLE IF NOT EXISTS `question` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` varchar(500) NOT NULL COMMENT '问题标题',
    `content` text NOT NULL COMMENT '问题内容',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    FULLTEXT KEY `idx_title_content` (`title`, `content`) COMMENT '全文索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试题目表';

-- 设置最大连接数
SET GLOBAL max_connections = 1000;

-- 设置全文索引最小搜索长度为1
SET GLOBAL innodb_ft_min_token_size = 1;

-- 刷新系统变量
FLUSH TABLES; 