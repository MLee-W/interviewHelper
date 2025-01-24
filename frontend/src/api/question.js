import axios from 'axios';

const api = {
  searchQuestions(keyword) {
    return axios.get('/api/questions/search', {
      params: { keyword }
    });
  },

  getQuestions(pageNum, pageSize) {
    return axios.get('/api/questions/list', {
      params: { pageNum, pageSize }
    });
  },

  uploadDocument(file) {
    const formData = new FormData();
    formData.append('file', file);
    return axios.post('/api/questions/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};

export default api; 