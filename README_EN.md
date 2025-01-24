
# **Interview Assistant**  
An efficient interview question management system with document parsing, keyword search, and theme switching functionality.

---

## **Project Overview**  
This project is a single-page application developed using **Vue.js** and **Element UI**, designed to help users efficiently manage and review interview questions. The system supports Word document parsing, automatically extracting questions and answers, and provides powerful search and convenient browsing features.

![image](https://github.com/user-attachments/assets/a0e82f97-3cac-48ae-ab5b-7ae681d37db6)


---

## **Key Features**
- **Document Upload & Parsing**: Supports `.doc` / `.docx` formats, automatically extracting questions and answers, with a file size limit of 50MB.
- **Smart Search**: Real-time keyword search with highlights and shortcut (`Ctrl + F`) for quick access.
- **Keyboard Navigation**: Navigate questions with ↑↓ keys and toggle answers with the `Enter` key.
- **Theme Switching**: Supports light and dark themes with auto-adaptation to system settings and persistent preferences.
- **Responsive Design**: Fully optimized for various screen sizes to enhance user experience.

---

## **Technology Stack**
- **Frontend Framework**: Vue.js 2.x  
- **UI Framework**: Element UI  
- **State Management**: Vuex  
- **Router Management**: Vue Router  
- **HTTP Client**: Axios  
- **CSS Preprocessors**: SCSS/LESS  

---

## **Project Structure**  
```
├── frontend/       # Frontend project directory
│   ├── src/
│   │   ├── api/     # API encapsulation
│   │   ├── assets/  # Static resources
│   │   ├── components/ # Common components
│   │   ├── router/  # Router configuration
│   │   ├── store/   # Vuex state management
│   │   ├── utils/   # Utility functions
│   │   ├── views/   # Page components
│   │   ├── App.vue  # Root component
│   │   └── main.js  # Entry file
│   └── public/      # Public static resources
├── backend/        # Backend project directory
│   ├── api/        # API implementation
│   ├── config/     # Configuration files
│   ├── models/     # Data models
│   ├── services/   # Business logic
│   └── utils/      # Utility functions
└── docs/           # Project documentation
```

---

## **Core Functionality**

### **1. Document Parsing**
- Supports `.doc` / `.docx` file uploads (file size ≤ 50MB).
- Automatically extracts the structure of questions and answers for easier management.

### **2. Search Functionality**
- Real-time keyword search with highlighted results.
- Shortcut `Ctrl + F` to quickly focus on the search bar.

### **3. Keyboard Navigation**
- **↑↓ keys**: Switch between questions.
- **Enter key**: Toggle the display of corresponding answers.
- Efficiently browse through content.

### **4. Theme Switching**
- Supports light and dark themes for better user experience.
- Preferences are stored locally and adapt to system settings.

### **5. UI/UX Design**
- **Responsive Layout**: Optimized for multiple devices.
- **Smooth Animations**: Enhances interactive experience.
- **Card-Based Display**: Clean and intuitive content layout.

---

## **Usage Instructions**

### **1. Upload Document**
- Click the "Upload Document" button in the top right corner of the page.
- Select a `.doc` or `.docx` file.
- Wait for the parsing to complete and browse the list of questions.

### **2. Search Questions**
- Enter keywords in the search bar at the top and press `Enter`.
- Use the shortcut `Ctrl + F` to quickly focus on the search bar.

### **3. Browse Questions**
- Use **↑↓ keys** to switch between questions.
- Press **Enter key** to toggle the display of answers.
- Alternatively, click the question title to view details.

### **4. Switch Theme**
- Click the theme toggle button in the top right corner of the page.
- The system automatically saves your theme preferences.

---

## **Development Environment**

- **Node.js** >= 12.x  
- **Vue CLI** >= 4.x  
- **npm** >= 6.x  

---

## **Installation & Deployment**

1. **Clone the Repository**
   ```bash
   git clone https://github.com/MLee-W/interviewHelper.git
   ```

2. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Start Development Server**
   ```bash
   npm run serve
   ```

4. **Build for Production**
   ```bash
   npm run build
   ```

---

## **Browser Support**
- **Recommended**: Chrome  
- **Supported**: Firefox, Safari, Edge  

---

## **Notes**
1. File size limit for uploads is 50MB.  
2. Only `.doc` and `.docx` formats are supported.  
3. It is recommended to use modern browsers for the best experience.  

---

## **Future Plans**
- [ ] Add user management system.  
- [ ] Support more document formats (e.g., PDF).  
- [ ] Add tag management functionality.  
- [ ] Provide export functionality for questions and answers.  
- [ ] Implement data analysis and statistics module.  

---

## **Contribution Guidelines**
Feel free to submit Issues and Pull Requests to contribute to the project!

---

## **License**
This project is open-sourced under the [MIT License](LICENSE).
