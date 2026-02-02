package com.example.ai.basics.day1.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

/**
 * =============================================================================
 * PART 2: BASIC PROMPT - Your First LLM Interaction
 * =============================================================================
 * 
 * LEARNING OBJECTIVES:
 * - Understand what a prompt is
 * - See how tokens are generated
 * - Learn about the context window
 * 
 * WHAT IS A PROMPT?
 * A prompt is the input text you send to an LLM. It's your "question" or
 * "instruction" to the model. The quality of your prompt directly affects
 * the quality of the response.
 * 
 * HOW TOKENS ARE GENERATED:
 * 1. Your prompt is tokenized (split into tokens - word pieces)
 * 2. The LLM processes these tokens through its neural network
 * 3. It predicts the next token based on probabilities
 * 4. This repeats until the response is complete (or max tokens reached)
 * 
 * CONTEXT WINDOW:
 * The context window is the maximum number of tokens the LLM can "see" at once.
 * This includes BOTH your prompt AND the generated response.
 * Example: GPT-4 has ~128K tokens, GPT-3.5 has ~16K tokens.
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/basic-prompt")
public class Part2BasicPromptController {

    /*
     * SPRING AI INJECTION:
     * ChatClient is the main interface for interacting with LLMs in Spring AI.
     * It's auto-configured based on your application.properties settings.
     * 
     * The ChatClient.Builder allows us to customize behavior like:
     * - System prompts (instructions that guide the LLM's behavior)
     * - Default options (temperature, max tokens, etc.)
     */
    private final ChatClient chatClient;

    public Part2BasicPromptController(ChatClient.Builder chatClientBuilder) {
        // Build the ChatClient with default configuration
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * EXERCISE 2.1: Your First Prompt
     * 
     * This is the simplest possible LLM interaction.
     * We send a fixed prompt and return the response.
     * 
     * TRY IT: GET http://localhost:8080/basic-prompt/hello
     * 
     * WHAT HAPPENS BEHIND THE SCENES:
     * 1. "Explain what is AI in one sentence" is tokenized
     * 2. Tokens are sent to OpenAI API
     * 3. LLM generates response tokens one by one
     * 4. Response tokens are decoded back to text
     */
    @GetMapping("/hello")
    public String helloAi() {
        // The prompt() method sends your text to the LLM
        // content() returns just the text response (not the full response object)
        return chatClient.prompt()
                .user("Explain what is AI in one sentence.")
                .call()
                .content();
    }

    /**
     * EXERCISE 2.2: Custom User Prompt
     * 
     * Now the user can provide their own prompt.
     * This demonstrates how LLMs handle any arbitrary input.
     * 
     * TRY IT: GET http://localhost:8080/basic-prompt/ask?prompt=What is machine
     * learning?
     * 
     * PROMPT TIP:
     * Notice how the same endpoint can answer wildly different questions.
     * The LLM uses its training data (corpus) to generate relevant responses.
     */
    @GetMapping("/ask")
    public String askAnything(@RequestParam String prompt) {
        return chatClient.prompt()
                .user(prompt) // User-provided prompt
                .call()
                .content();
    }

    /**
     * EXERCISE 2.3: System Prompt - Setting the LLM's Personality
     * 
     * A system prompt sets the "context" or "persona" for the LLM.
     * It's like giving the model a role to play or rules to follow.
     * 
     * TRY IT:
     * GET http://localhost:8080/basic-prompt/with-system?prompt=Hello!
     * 
     * COMPARE: Try the same prompt with /ask and /with-system
     * Notice how the response style changes based on the system prompt!
     */
    @GetMapping("/with-system")
    public String askWithSystemPrompt(@RequestParam String prompt) {
        return chatClient.prompt()
                // System prompt: Defines HOW the LLM should respond
                .system("You are a friendly, enthusiastic AI teacher. " +
                        "Explain concepts simply, use analogies, and be encouraging. " +
                        "Always end with an interesting fact!")
                // User prompt: The actual question/request
                .user(prompt)
                .call()
                .content();
    }

    /**
     * EXERCISE 2.4: Observing Token Behavior
     * 
     * This endpoint shows a simple demonstration of how responses vary.
     * Each call may produce slightly different responses because of:
     * - Temperature (randomness)
     * - The probabilistic nature of token selection
     * 
     * TRY IT: Call this multiple times and compare responses
     * GET http://localhost:8080/basic-prompt/token-demo
     */
    @GetMapping("/token-demo")
    public String tokenDemo() {
        String prompt = """
                Complete this sentence in exactly 10 words:
                "The future of artificial intelligence is..."
                """;

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // Note: We can't directly see tokens in the response, but we can
        // estimate: ~4 characters per token on average for English text
        int estimatedTokens = response.length() / 4;

        return String.format("""
                PROMPT: %s

                RESPONSE: %s

                STATS:
                - Response length: %d characters
                - Estimated tokens: ~%d tokens

                TIP: Run this multiple times to see how responses vary!
                """, prompt.trim(), response, response.length(), estimatedTokens);
    }
}
