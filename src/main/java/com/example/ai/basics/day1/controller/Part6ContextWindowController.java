package com.example.ai.basics.day1.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.*;

/**
 * =============================================================================
 * PART 6: CONTEXT WINDOW - Understanding LLM Memory Limits
 * =============================================================================
 * 
 * LEARNING OBJECTIVES:
 * - Understand what the context window is
 * - See how context limits affect LLM behavior
 * - Learn strategies for managing long contexts
 * 
 * WHAT IS THE CONTEXT WINDOW?
 * The context window is the maximum number of tokens an LLM can process at
 * once.
 * This includes BOTH the input (your prompt) AND the output (the response).
 * 
 * EXAMPLES:
 * - GPT-3.5-turbo: ~16K tokens
 * - GPT-4: ~8K or ~128K tokens (depending on version)
 * - GPT-4o: ~128K tokens
 * - Claude 3: ~200K tokens
 * 
 * WHY DOES THIS MATTER?
 * 1. Long documents may not fit entirely
 * 2. The LLM "forgets" information outside its window
 * 3. Earlier parts of long prompts may be ignored
 * 4. Cost increases with token count
 * 
 * STRATEGIES FOR LONG CONTENT:
 * 1. Chunking: Split into smaller pieces
 * 2. Summarization: Compress before processing
 * 3. RAG: Retrieve only relevant parts
 * 4. Truncation: Keep most important parts
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/context-window")
public class Part6ContextWindowController {

    private final ChatClient chatClient;

    public Part6ContextWindowController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * EXERCISE 6.1: Demonstrate Context Retention
     * 
     * This shows how the LLM remembers information within a single prompt.
     * 
     * TRY IT: GET http://localhost:8080/context-window/retention-test
     */
    @GetMapping("/retention-test")
    public String contextRetentionTest() {
        // Create a prompt with specific information to remember
        String prompt = """
                I will give you some specific information. Then I'll ask questions about it.

                INFORMATION:
                - My name is Alex
                - I live in Barcelona
                - My favorite color is purple
                - I have a cat named Luna
                - My birthday is March 15th
                - I work as a software engineer

                Now answer these questions:
                1. What is my name?
                2. Where do I live?
                3. What's my pet's name?
                4. What do I do for work?
                """;

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return String.format("""
                =================================================================
                CONTEXT RETENTION TEST
                =================================================================

                This demonstrates that the LLM can "remember" information
                given earlier in the same prompt (within the context window).

                PROMPT SENT:
                %s

                LLM RESPONSE:
                %s

                =================================================================
                📚 KEY INSIGHT:
                The LLM successfully recalls the information because it's all
                within the context window. Each token can "attend to" (see)
                every other token thanks to the attention mechanism.

                This is how LLMs can:
                - Follow multi-step instructions
                - Answer questions about provided text
                - Maintain conversation coherence
                =================================================================
                """, prompt, response);
    }

    /**
     * EXERCISE 6.2: Simulating Context Overflow
     * 
     * This demonstrates what happens when we limit the response tokens.
     * While not true context overflow, it shows truncation behavior.
     * 
     * TRY IT: GET http://localhost:8080/context-window/truncation-demo
     */
    @GetMapping("/truncation-demo")
    public String truncationDemo() {
        String prompt = "List 20 interesting facts about space, numbered from 1 to 20.";

        // Very limited response tokens - will cause truncation
        String truncatedResponse = chatClient.prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder()
                        .maxTokens(100) // Very limited
                        .build())
                .call()
                .content();

        // Normal response with more tokens
        String fullResponse = chatClient.prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder()
                        .maxTokens(1000) // Enough for full response
                        .build())
                .call()
                .content();

        return String.format("""
                =================================================================
                TRUNCATION DEMO (Max Tokens Limit)
                =================================================================

                PROMPT: "%s"

                -----------------------------------------------------------------
                RESPONSE WITH MAX_TOKENS = 100 (truncated):
                -----------------------------------------------------------------
                %s

                [Response was cut off due to token limit!]

                -----------------------------------------------------------------
                RESPONSE WITH MAX_TOKENS = 1000 (complete):
                -----------------------------------------------------------------
                %s

                =================================================================
                📚 THIS ILLUSTRATES:
                - max_tokens limits the OUTPUT length
                - The response simply stops when limit is reached
                - Important information might be cut off

                REAL CONTEXT WINDOW ISSUES:
                - With very long inputs, the LLM may:
                  1. Ignore early content ("lost in the middle" problem)
                  2. Fail to follow all instructions
                  3. Miss important details

                SOLUTIONS:
                - Chunking: Split long documents
                - Summarization: Compress content first
                - RAG: Only include relevant sections
                =================================================================
                """, prompt, truncatedResponse, fullResponse);
    }

    /**
     * EXERCISE 6.3: Position Effect Demo
     * 
     * This demonstrates the "lost in the middle" phenomenon.
     * LLMs tend to remember the beginning and end better than the middle.
     * 
     * TRY IT: GET http://localhost:8080/context-window/position-effect
     */
    @GetMapping("/position-effect")
    public String positionEffectDemo() {
        // Place a secret code in different positions
        String prompt = """
                Read the following text carefully and find the SECRET CODE.

                BEGINNING SECTION:
                The secret code is: ALPHA-123
                Lorem ipsum dolor sit amet, consectetur adipiscing elit.

                MIDDLE SECTION (lots of filler text):
                Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.
                The secret code is: BETA-456
                Duis aute irure dolor in reprehenderit in voluptate velit esse.
                Excepteur sint occaecat cupidatat non proident sunt in culpa.
                Nisi ut aliquip ex ea commodo consequat quis nostrud exercitation.

                END SECTION:
                Cillum dolore eu fugiat nulla pariatur.
                The secret code is: GAMMA-789

                QUESTION: What are ALL the secret codes you found? List them all.
                """;

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return String.format("""
                =================================================================
                POSITION EFFECT DEMO ("Lost in the Middle")
                =================================================================

                We placed 3 secret codes in different positions:
                - BEGINNING: ALPHA-123
                - MIDDLE: BETA-456
                - END: GAMMA-789

                LLM RESPONSE:
                %s

                =================================================================
                📚 RESEARCH FINDING: "Lost in the Middle"

                Studies show LLMs perform better at recalling information from:
                - The BEGINNING of the context (primacy effect)
                - The END of the context (recency effect)

                Information in the MIDDLE is more likely to be missed!

                PRACTICAL IMPLICATIONS:
                1. Put important instructions at the END of your prompt
                2. Repeat critical information
                3. Use clear section markers
                4. Consider chunking long documents

                REFERENCE:
                "Lost in the Middle: How Language Models Use Long Contexts"
                (Liu et al., 2023)
                =================================================================
                """, response);
    }

    /**
     * EXERCISE 6.4: Estimate Token Count
     * 
     * A rough tool to help understand tokenization.
     * 
     * TRY IT: GET http://localhost:8080/context-window/estimate-tokens?text=Hello
     * world, how are you today?
     */
    @GetMapping("/estimate-tokens")
    public String estimateTokens(@RequestParam String text) {
        // Rough estimation: ~4 characters per token for English
        // This is a simplification - real tokenization is more complex
        int charCount = text.length();
        int wordCount = text.split("\\s+").length;
        int estimatedTokens = Math.max(1, charCount / 4);

        // Alternative estimation based on words
        int tokensByWords = (int) (wordCount * 1.3); // ~1.3 tokens per word

        return String.format("""
                =================================================================
                TOKEN ESTIMATION
                =================================================================

                INPUT TEXT: "%s"

                STATISTICS:
                - Characters: %d
                - Words: %d

                ESTIMATED TOKENS:
                - By characters (~4 chars/token): ~%d tokens
                - By words (~1.3 tokens/word): ~%d tokens

                =================================================================
                📚 HOW TOKENIZATION WORKS:

                LLMs don't see text as characters or words. They see TOKENS.
                Tokens are "word pieces" learned during training.

                EXAMPLES:
                - "hello" → 1 token
                - "unbelievable" → might be ["un", "believ", "able"] → 3 tokens
                - "123456" → varies by model

                WHY IT MATTERS:
                - Billing is per token
                - Context window is measured in tokens
                - Different languages have different token efficiency
                  (English is efficient, other languages may use more)

                TIP: OpenAI provides a tokenizer tool:
                https://platform.openai.com/tokenizer
                =================================================================
                """, text, charCount, wordCount, estimatedTokens, tokensByWords);
    }
}
