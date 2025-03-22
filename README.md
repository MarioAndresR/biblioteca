# 📚 Biblioteca - Library Management System

Welcome to **Biblioteca**, a simple Library Management System built as a full-stack web application using Java and Spring Boot. 
This project was developed to practice core backend concepts such as CRUD operations, security, user management, and database interaction.

## 🚀 Project Overview

The Biblioteca system allows users to:
- Add, update, view, and delete books.
- Manage authors and publishers.
- Handle user registration, login, and roles (Admin/User).
- Upload and display profile images.
- Secure different functionalities based on user roles.

## 🛠️ Technologies Used

This project is built with the following technologies:
- **Java 17**
- **Spring Boot 3**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Security
- **MySQL** (Relational database)
- **Maven** (Project management)
- **Lombok** (Code simplification)
- **Thymeleaf** (Server-side rendering)
- **Bootstrap 5** (Styling and responsive design)

## 🔒 Features
- User registration and authentication (Login)
- Role-based access control (ADMIN / USER)
- CRUD operations for books, authors, and publishers
- File upload for user profile images
- Responsive user interface using Bootstrap
- Error handling and form validation

## 🗄️ Database Schema
The system uses a MySQL database with the following main entities:
- `Usuario` (User)
- `Libro` (Book)
- `Autor` (Author)
- `Editorial` (Publisher)
- `Imagen` (Profile Image)

## 📷 Screenshots
![image](https://github.com/user-attachments/assets/8d72f74d-b3e4-4839-ba9d-1a0f10af1126)

![image](https://github.com/user-attachments/assets/de25aed7-723c-4534-9310-1efb058ee711)

## 🚀 How to Run the Project
1. Clone the repository:
   git clone https://github.com/MarioAndresR/biblioteca.git
   cd biblioteca
2. Set up your MySQL database and configure application.properties
3. Run the project:
  ./mvnw spring-boot:run
4. Access the app at http://localhost:8080

## 🧠 Author
Mario Andrés Rodríguez Osorio
Electronics Engineer | Backend Developer (PL/SQL, Java)
Passionate about backend development, computer vision, and data science.

- **💼 LinkedIn Profile**
- **📂 My GitHub**

## 🤝 Contributing
Contributions are welcome! Feel free to fork the repository and submit pull requests.
