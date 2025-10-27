# AI Chain Learning Room

An online examination and video learning system based on Spring Boot, featuring AI-powered test paper generation, automatic grading, video management, and more.

## Features

- **Exam System**: Supports AI-powered test generation, online exams, automatic grading, and exam record management
- **Question Bank Management**: Full CRUD operations for questions, category management, Excel import, and AI-generated questions
- **Video Learning**: Video upload and management, category organization, viewing statistics, likes, and favorites
- **Content Management**: Carousel banner management, system announcements, data analytics dashboard
- **File Storage**: Integrated with MinIO for file upload and download

## Technology Stack

- Spring Boot + MyBatis Plus
- Knife4j API documentation
- MinIO file storage
- MySQL database
- Redis caching
- Lombok & Swagger2

## Module Structure

- **Controller Layer**: RESTful API design with permission verification
- **Service Layer**: CRUD operations based on MyBatis Plus
- **Data Models**: Core entities including exams, test papers, questions, and videos
- **Utility Classes**: Excel processing, IP tools, response data encapsulation

## API Documentation

The system provides comprehensive API documentation covering:
- User authentication APIs
- Exam management APIs
- Question bank management APIs
- Video management APIs
- Data statistics APIs
- System settings APIs

## Installation and Deployment

1. Clone the project: `git clone https://gitee.com/AimerUna/ai-chain-learning-room.git`
2. Create a MySQL database and import the schema
3. Configure database connection and MinIO parameters in `application.yml`
4. Run `mvn spring-boot:run` to start the application

## Usage Instructions

The system includes two admin interfaces:
- **Exam Management Portal**: Handles test paper generation, exam records, and question bank maintenance
- **Video Management Portal**: Manages video content, categories, and review processes

## Contribution Guidelines

Code contributions are welcome. Please follow these guidelines:
1. Fork the repository and create a feature branch
2. Ensure code complies with Java coding standards
3. Add necessary comments and documentation
4. Submit a pull request with a clear description of changes

## License

This project is licensed under the Apache-2.0 open source license. Please use it in compliance with the terms of the license.