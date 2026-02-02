package com.example.ai.basics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =============================================================================
 * AI BASICS - EDUCATIONAL SPRING AI APPLICATION
 * =============================================================================
 * 
 * This application demonstrates Generative AI fundamentals using Spring AI.
 * 
 * WHAT HAPPENS WHEN THIS STARTS:
 * 1. Spring Boot scans for @Component, @Service, @Controller, @RestController
 * 2. Spring AI auto-configures the ChatClient and EmbeddingModel beans
 * 3. These beans connect to the LLM provider (OpenAI) using
 * application.properties
 * 
 * KEY SPRING AI COMPONENTS:
 * - ChatClient: Sends prompts to LLM, receives completions (text generation)
 * - EmbeddingModel: Converts text to vector embeddings (numerical
 * representations)
 * 
 * LEARNING PATH (Endpoints to explore in order):
 * 1. /basic-prompt - Simple prompt, understanding tokens & context
 * 2. /prompt-templates - Prompt engineering with templates
 * 3. /generation-params - Temperature, Top-K, Top-P experimentation
 * 4. /embeddings - Text to vectors, similarity search
 * 5. /context-window - Understanding LLM context limitations
 * 6. /study-assistant - Integrated exercise combining all concepts
 * 
 * @author AI Basics Educational Team
 */
@SpringBootApplication
public class AiBasicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiBasicsApplication.class, args);
	}

}
