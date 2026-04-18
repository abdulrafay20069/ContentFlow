# ContentFlow AI

A full-stack AI content generation web application.

**Live Demo:** https://content-flow-frontend-lemon.vercel.app

## Features
- Generate blog posts, social media posts, emails, product descriptions
- Summarize any text
- Powered by Groq API (Llama 3.3 70B)

## Tech Stack
- Backend: Java 17, Spring Boot, OkHttp
- Frontend: React
- AI: Groq API
- Deployed: Railway (backend) + Vercel (frontend)

## Setup
1. Clone the repo
2. Create `src/main/resources/application.properties`
3. Add: `GROQ_API_KEY=your_key_here`
4. Run: `mvn spring-boot:run`