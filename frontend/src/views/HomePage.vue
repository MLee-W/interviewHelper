<template>
  <div class="home" @click="handleGlobalClick" :class="theme">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header height="60px">
        <div class="header-content">
          <div class="logo">
            <i class="el-icon-s-opportunity"></i>
            面试助手
          </div>
          <div class="search-container">
            <el-input
              v-model="searchKeyword"
              placeholder="请输入关键词搜索（Ctrl + F）"
              @keyup.enter.native="handleSearch"
              ref="searchInput"
              clearable
              prefix-icon="el-icon-search"
              size="medium"
            >
            </el-input>
          </div>
          <div class="right-container">
            <div class="theme-switch">
              <el-switch
                v-model="isDarkTheme"
                active-color="#13ce66"
                inactive-color="#409EFF"
                active-icon-class="el-icon-moon"
                inactive-icon-class="el-icon-sunny"
                @change="toggleTheme"
              >
              </el-switch>
            </div>
            <div class="upload-container">
              <el-upload
                class="upload-demo"
                :action="null"
                :http-request="customUpload"
                :before-upload="beforeUpload"
                :show-file-list="false"
                accept=".doc,.docx"
              >
                <el-button type="primary" icon="el-icon-upload2">上传文档</el-button>
              </el-upload>
            </div>
          </div>
        </div>
      </el-header>

      <!-- 主体内容区 -->
      <el-main>
        <div class="content-container">
          <!-- 问题列表区域 -->
          <div class="question-list"
               ref="questionList"
               tabindex="0"
               @keydown.up.prevent="handleKeyUp"
               @keydown.down.prevent="handleKeyDown"
               @keydown.enter.prevent="handleKeyEnter">
            <el-card v-for="(item, index) in questions"
                     :key="item.id"
                     :class="{ 'selected': selectedIndex === index }"
                     @click.stop="handleQuestionClick(index)"
                     shadow="hover">
              <div class="question-title" @click.stop="toggleAnswer(index)">
                <span class="question-index">#{{ index + 1 }}</span>
                <span class="title-text">{{ item.title }}</span>
                <i :class="['expand-icon', 'el-icon-arrow-right', {'expanded': expandedIndex === index}]"></i>
              </div>
              <div class="question-content" v-show="expandedIndex === index">
                <pre>{{ item.content }}</pre>
              </div>
            </el-card>

            <!-- 空状态 -->
            <div v-if="questions.length === 0" class="empty-state">
              <i class="el-icon-search"></i>
              <p>暂无数据</p>
            </div>

            <!-- 分页器 -->
            <div class="pagination-container" v-if="!searchKeyword && questions.length > 0">
              <el-pagination
                @current-change="handlePageChange"
                :current-page.sync="currentPage"
                :page-size="pageSize"
                layout="total, prev, pager, next, jumper"
                :total="total"
                background>
              </el-pagination>
            </div>
          </div>
        </div>
      </el-main>

      <!-- 底部版权信息 -->
      <el-footer height="40px">
        <div class="footer-content">
          <span>© 2024 面试助手</span>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import api from '../api/question';

export default {
  name: 'HomePage',
  data() {
    return {
      searchKeyword: '',
      questions: [],
      selectedIndex: -1,
      expandedIndex: -1,
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      isListFocused: false,
      isDarkTheme: false,
      theme: 'theme-light'
    };
  },

  created() {
    // 从 localStorage 读取主题设置
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
      this.theme = savedTheme;
      this.isDarkTheme = savedTheme === 'theme-dark';
    }
    this.loadQuestions();
  },

  mounted() {
    document.addEventListener('keydown', this.handleGlobalKeyDown);
  },

  beforeDestroy() {
    document.removeEventListener('keydown', this.handleGlobalKeyDown);
  },

  methods: {
    async loadQuestions() {
      try {
        this.loading = true;
        const response = await api.getQuestions(this.currentPage, this.pageSize);
        this.questions = response.data.list;
        this.total = response.data.total;
      } catch (error) {
        this.$message.error('加载失败：' + error.message);
      } finally {
        this.loading = false;
      }
    },

    async handleSearch() {
      if (!this.searchKeyword.trim()) {
        await this.loadQuestions();
        return;
      }
      
      try {
        this.loading = true;
        const response = await api.searchQuestions(this.searchKeyword);
        this.questions = response.data;
        
        this.$nextTick(() => {
          this.$refs.searchInput.blur();
          if (this.questions.length > 0) {
            this.selectedIndex = 0;
            this.$refs.questionList.focus();
            this.isListFocused = true;
            
            // 操作问题列表的滚动条
            this.$refs.questionList.scrollTop = 0;
          } else {
            this.selectedIndex = -1;
          }
        });
      } catch (error) {
        this.$message.error('搜索失败：' + error.message);
      } finally {
        this.loading = false;
      }
    },

    async handlePageChange(page) {
      this.currentPage = page;
      await this.loadQuestions();
      this.selectedIndex = -1;
      this.expandedIndex = -1;
    },

    handleGlobalClick() {
      // 点击列表区域外时取消选中
      if (!this.$refs.questionList.contains(event.target)) {
        this.selectedIndex = -1;
        this.isListFocused = false;
      }
    },

    handleQuestionClick(index) {
      this.selectedIndex = index;
      this.isListFocused = true;
      this.$refs.questionList.focus();
    },

    handleKeyUp() {
      if (this.isListFocused && this.questions.length > 0) {
        if (this.selectedIndex === -1) {
          this.selectedIndex = 0;
        } else if (this.selectedIndex > 0) {
          this.selectedIndex--;
        }
        this.scrollToQuestion(this.selectedIndex);
      }
    },

    handleKeyDown() {
      if (this.isListFocused && this.questions.length > 0) {
        if (this.selectedIndex === -1) {
          this.selectedIndex = 0;
        } else if (this.selectedIndex < this.questions.length - 1) {
          this.selectedIndex++;
        }
        this.scrollToQuestion(this.selectedIndex);
      }
    },

    handleKeyEnter() {
      if (this.isListFocused && this.selectedIndex !== -1) {
        this.toggleAnswer(this.selectedIndex);
      }
    },

    handleGlobalKeyDown(event) {
      if (event.ctrlKey && event.key === 'f') {
        event.preventDefault();
        this.searchKeyword = '';
        this.$nextTick(() => {
          this.$refs.searchInput.focus();
          this.isListFocused = false;
        });
      }
    },

    scrollToQuestion(index) {
      this.$nextTick(() => {
        const cards = this.$el.getElementsByClassName('el-card');
        if (cards[index]) {
          // 获取顶部导航栏的高度
          const headerHeight = 60; // 导航栏高度
          const mainPadding = 20; // 内容区域的上padding
          const cardTop = cards[index].getBoundingClientRect().top;
          const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

          // 计算需要滚动的位置，确保卡片在导航栏下方
          const targetPosition = scrollTop + cardTop - headerHeight - mainPadding;

          window.scrollTo({
            top: targetPosition,
            behavior: 'smooth'
          });
        }
      });
    },

    toggleAnswer(index) {
      this.expandedIndex = this.expandedIndex === index ? -1 : index;
    },

    async customUpload({ file }) {
      try {
        this.loading = true;
        const response = await api.uploadDocument(file);
        this.$message.success('文档上传成功');
        this.questions = response.data;
        this.selectedIndex = this.questions.length > 0 ? 0 : -1;
      } catch (error) {
        this.$message.error('上传失败：' + error.message);
      } finally {
        this.loading = false;
      }
    },

    beforeUpload(file) {
      const isDoc = file.type === 'application/msword' ||
                   file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
      const isLt50M = file.size / 1024 / 1024 < 50;

      if (!isDoc) {
        this.$message.error('只能上传Word文档！');
        return false;
      }
      if (!isLt50M) {
        this.$message.error('文件大小不能超过50MB！');
        return false;
      }
      return true;
    },

    toggleTheme() {
      this.theme = this.isDarkTheme ? 'theme-dark' : 'theme-light';
      localStorage.setItem('theme', this.theme);
    }
  }
};
</script>

<style scoped>
.home {
  height: 100vh;
  background-color: var(--bg-color);
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: fixed;
  width: 100%;
  z-index: 100;
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-color);
  margin-right: 40px;
  display: flex;
  align-items: center;
}

.logo i {
  margin-right: 8px;
  font-size: 24px;
}

.search-container {
  flex: 1;
  max-width: 500px;
  margin-right: 20px;
}

.search-container .el-input__inner {
  border-radius: 20px;
}

.right-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.theme-switch {
  margin-right: 10px;
}

.el-main {
  padding-top: 80px;
  padding-bottom: 60px;
  height: 100vh;  /* 限制主体区域高度 */
  overflow: hidden;  /* 防止主体区域溢出 */
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;  /* 确保容器填满主体区域 */
}

.question-list {
  outline: none;
  padding-top: 20px;
  cursor: default;
  position: relative;
  overflow-y: auto;
  max-height: calc(100vh - 200px);
}

.el-card {
  margin-bottom: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: var(--card-bg);
  border-color: var(--border-color);
}

.el-card:hover {
  transform: translateY(-2px);
  background-color: var(--hover-color);
}

.el-card.selected {
  border: 2px solid #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
}

.question-title {
  display: flex;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
  color: var(--text-color);
}

.question-index {
  color: var(--text-secondary);
  margin-right: 12px;
  font-size: 14px;
}

.title-text {
  flex: 1;
  font-weight: 500;
  color: var(--text-color);
}

.expand-icon {
  color: var(--text-secondary);
  transition: transform 0.3s ease;
  font-size: 16px;
}

.expand-icon.expanded {
  transform: rotate(90deg);
}

.question-content {
  margin-top: 16px;
  padding: 16px;
  background-color: var(--hover-color);
  border-radius: 4px;
}

.question-content pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-family: inherit;
  color: var(--text-color);
  line-height: 1.6;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: var(--text-secondary);
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.pagination-container {
  margin-top: 20px;
  margin-bottom: 20px;
  text-align: center;
}

/* 滚动条美化 */
.question-list::-webkit-scrollbar {
  width: 6px;
}

.question-list::-webkit-scrollbar-thumb {
  background-color: #909399;
  border-radius: 3px;
}

.question-list::-webkit-scrollbar-track {
  background-color: transparent;
}

.el-footer {
  background-color: var(--footer-bg);
  border-top: 1px solid var(--border-color);
  position: fixed;
  bottom: 0;
  width: 100%;
  z-index: 100;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  font-size: 14px;
}

/* 添加响应式布局 */
@media screen and (max-width: 768px) {
  .header-content {
    flex-direction: column;
    padding: 10px;
    height: auto;
  }

  .logo {
    margin-bottom: 10px;
    margin-right: 0;
  }

  .search-container {
    width: 100%;
    max-width: none;
    margin-right: 0;
    margin-bottom: 10px;
  }

  .upload-container {
    width: 100%;
    display: flex;
    justify-content: center;
  }

  .el-main {
    padding-top: 160px;
  }

  .question-list {
    padding-top: 10px;
  }
}

/* 确保列表区域可以接收焦点 */
.question-list[tabindex="0"] {
  user-select: none;
}

/* 主题相关样式 */
.theme-dark .el-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
}

.theme-dark .el-card.selected {
  border: 2px solid #409EFF;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.2);
}

.theme-dark .question-content {
  border: 1px solid var(--border-color);
}

/* 确保输入框在暗色主题下也易读 */
.theme-dark .el-input__inner {
  background-color: var(--card-bg);
  border-color: var(--border-color);
  color: var(--text-color);
}

.theme-dark .el-input__inner::placeholder {
  color: var(--text-secondary);
}

/* 亮色主题 */
.theme-light {
  --bg-color: #f5f7fa;
  --header-bg: #ffffff;
  --card-bg: #fff;
  --text-color: #303133;
  --text-secondary: #606266;
  --border-color: #e4e7ed;
  --hover-color: #f5f7fa;
  --footer-bg: #fff;
}

/* 暗色主题 */
.theme-dark {
  --bg-color: #1a1a1a;
  --header-bg: #1d1d1d;  /* 更深的背景色 */
  --card-bg: #2c2c2c;
  --text-color: #ffffff;
  --text-secondary: #a8a8a8;
  --border-color: #4a4a4a;
  --hover-color: #363636;
  --footer-bg: #2c2c2c;
}
</style>
