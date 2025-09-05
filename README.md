# Spring Boot Gemini API Client

This is a Spring Boot application that connects to **Google Gemini API**.

## Setup

1. Get a Gemini API key from [Google AI Studio](https://ai.google.dev/gemini-api).
2. Export it as an environment variable:

   ```bash
   export GEMINI_API_KEY="your_api_key_here"
   ```

3. Build and run:

   ```bash
   mvn clean package
   java -jar target/springboot-gemini-1.0.0.jar
   ```

4. Test API:

   ```bash
   curl -X POST http://localhost:8080/api/ask/session1 \
     -H "Content-Type: application/json" \
     -d '{"prompt":"whats my name"}'
   ```

## Notes
- The model used is `gemini-1.5-flash`. You can replace with `gemini-2.5-pro` or `deep-think` (once available).
