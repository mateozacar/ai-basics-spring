package com.example.ai.basics.day1.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.*;

/**
 * =============================================================================
 * PART 4: GENERATION PARAMETERS - Temperature, Top-K, and Top-P
 * =============================================================================
 * 
 * LEARNING OBJECTIVES:
 * - Understand how LLMs select the next token
 * - Learn what temperature, top-k, and top-p control
 * - Know when to use deterministic vs creative settings
 * 
 * HOW TOKEN SELECTION WORKS:
 * 1. The LLM generates a probability distribution over all possible next tokens
 * 2. "The" might have 30% probability, "A" 20%, "It" 15%, etc.
 * 3. Parameters like temperature, top-k, top-p modify which tokens get selected
 * 
 * TEMPERATURE (0.0 - 2.0):
 * Controls the "sharpness" of the probability distribution.
 * - Low (0.0-0.3): Model picks high-probability tokens → deterministic, focused
 * - Medium (0.5-0.7): Balanced exploration → good for most tasks
 * - High (0.8-1.5): More random selection → creative, but may be incoherent
 * 
 * TOP-K (integer):
 * Limits selection to the K most probable tokens.
 * - Top-K = 1: Always pick the most probable (greedy decoding)
 * - Top-K = 10: Choose from top 10 tokens only
 * - Top-K = 50: Default for some models, good diversity
 * 
 * TOP-P / Nucleus Sampling (0.0 - 1.0):
 * Limits selection to tokens whose cumulative probability reaches P.
 * - Top-P = 0.1: Very focused (only ~10% probability mass)
 * - Top-P = 0.9: Diverse (top 90% probability mass)
 * - Top-P = 1.0: Consider all tokens (disabled)
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/generation-params")
public class Part4GenerationParamsController {

    private final ChatClient chatClient;

    public Part4GenerationParamsController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * EXERCISE 4.1: Temperature Comparison
     * 
     * Same prompt with different temperatures shows the creativity spectrum.
     * 
     * TRY IT: GET http://localhost:8080/generation-params/temperature-compare
     * 
     * EXPECTED BEHAVIOR:
     * - Temperature 0.0: Very consistent, predictable response
     * - Temperature 0.7: Balanced, still coherent but varied
     * - Temperature 1.5: More creative/random, potentially surprising
     */
    @GetMapping("/temperature-compare")
    public String compareTemperatures() {
        String prompt = "Write a one-sentence opening for a story about a robot.";
        StringBuilder result = new StringBuilder();

        // Different temperature values to compare
        double[] temperatures = { 0.0, 0.5, 1.0, 1.5 };

        result.append("=================================================================\n");
        result.append("TEMPERATURE COMPARISON\n");
        result.append("Prompt: \"").append(prompt).append("\"\n");
        result.append("=================================================================\n\n");

        for (double temp : temperatures) {
            String response = chatClient.prompt()
                    .user(prompt)
                    .options(OpenAiChatOptions.builder()
                            .temperature(temp)
                            .build())
                    .call()
                    .content();

            result.append(String.format("🌡️ Temperature %.1f:\n%s\n\n", temp, response));
        }

        result.append("""
                =================================================================
                📚 KEY OBSERVATIONS:
                - Low temp (0.0): Most predictable, "safe" choice
                - Medium temp (0.5-0.7): Good balance for most use cases
                - High temp (1.0+): More creative but may lose coherence

                USE CASES:
                - Code generation: 0.0-0.3 (need accuracy)
                - Chatbots: 0.5-0.7 (natural but reliable)
                - Creative writing: 0.8-1.2 (want variety)
                =================================================================
                """);

        return result.toString();
    }

    /**
     * EXERCISE 4.2: Custom Generation Parameters
     * 
     * Allows experimenting with temperature, top-k, and top-p.
     * 
     * TRY THESE:
     * GET
     * http://localhost:8080/generation-params/custom?temperature=0&prompt=Complete
     * this: The capital of France is
     * GET
     * http://localhost:8080/generation-params/custom?temperature=1.5&prompt=Invent
     * a new word and define it
     * GET
     * http://localhost:8080/generation-params/custom?temperature=0.7&topP=0.5&prompt=Write
     * a haiku about coding
     */
    @GetMapping("/custom")
    public String customParameters(
            @RequestParam String prompt,
            @RequestParam(defaultValue = "0.7") Double temperature,
            @RequestParam(defaultValue = "1.0") Double topP) {

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .temperature(temperature)
                .topP(topP)
                .build();

        String response = chatClient.prompt()
                .user(prompt)
                .options(options)
                .call()
                .content();

        return String.format("""
                =================================================================
                CUSTOM GENERATION PARAMETERS
                =================================================================

                PARAMETERS USED:
                - Temperature: %.2f%s
                - Top-P: %.2f%s

                PROMPT:
                %s

                RESPONSE:
                %s

                =================================================================
                PARAMETER EFFECTS:
                - Temperature %.2f: %s
                - Top-P %.2f: %s
                =================================================================
                """,
                temperature, getTemperatureDescription(temperature),
                topP, getTopPDescription(topP),
                prompt,
                response,
                temperature, getTemperatureEffect(temperature),
                topP, getTopPEffect(topP));
    }

    /**
     * EXERCISE 4.3: Deterministic Output Demo
     * 
     * Run this multiple times with temperature=0 to see consistent results.
     * 
     * TRY IT:
     * GET http://localhost:8080/generation-params/deterministic?runs=3
     * 
     * EXPECTED: All runs should produce similar (or identical) outputs
     */
    @GetMapping("/deterministic")
    public String deterministicDemo(@RequestParam(defaultValue = "3") int runs) {
        String prompt = "What is 2 + 2? Reply with just the number.";
        StringBuilder result = new StringBuilder();

        result.append("=================================================================\n");
        result.append("DETERMINISTIC OUTPUT DEMO (Temperature = 0.0)\n");
        result.append("=================================================================\n\n");
        result.append("Prompt: \"").append(prompt).append("\"\n\n");

        for (int i = 1; i <= runs; i++) {
            String response = chatClient.prompt()
                    .user(prompt)
                    .options(OpenAiChatOptions.builder()
                            .temperature(0.0)
                            .build())
                    .call()
                    .content();

            result.append(String.format("Run %d: %s\n", i, response.trim()));
        }

        result.append("""

                =================================================================
                📚 OBSERVATION:
                With temperature=0, the LLM always picks the highest probability
                token, making outputs consistent across runs.

                USE THIS FOR:
                - Factual Q&A
                - Code generation
                - Data extraction
                - Any task requiring reproducibility
                =================================================================
                """);

        return result.toString();
    }

    /**
     * EXERCISE 4.4: Creative Output Demo
     * 
     * Run this multiple times with high temperature to see varied results.
     * 
     * TRY IT: GET http://localhost:8080/generation-params/creative?runs=3
     */
    @GetMapping("/creative")
    public String creativeDemo(@RequestParam(defaultValue = "3") int runs) {
        String prompt = "Invent a creative name for a coffee shop and explain it in one sentence.";
        StringBuilder result = new StringBuilder();

        result.append("=================================================================\n");
        result.append("CREATIVE OUTPUT DEMO (Temperature = 1.2)\n");
        result.append("=================================================================\n\n");
        result.append("Prompt: \"").append(prompt).append("\"\n\n");

        for (int i = 1; i <= runs; i++) {
            String response = chatClient.prompt()
                    .user(prompt)
                    .options(OpenAiChatOptions.builder()
                            .temperature(1.2)
                            .build())
                    .call()
                    .content();

            result.append(String.format("Run %d:\n%s\n\n", i, response.trim()));
        }

        result.append("""
                =================================================================
                📚 OBSERVATION:
                With high temperature, responses vary significantly!
                Lower probability tokens have a better chance of being selected.

                USE THIS FOR:
                - Brainstorming
                - Creative writing
                - Generating alternatives
                - Any task wanting unpredictability
                =================================================================
                """);

        return result.toString();
    }

    // Helper methods for descriptions
    private String getTemperatureDescription(Double temp) {
        if (temp <= 0.3)
            return " (very deterministic)";
        if (temp <= 0.7)
            return " (balanced)";
        if (temp <= 1.0)
            return " (creative)";
        return " (very random)";
    }

    private String getTopPDescription(Double topP) {
        if (topP <= 0.3)
            return " (very focused)";
        if (topP <= 0.7)
            return " (selective)";
        if (topP <= 0.9)
            return " (diverse)";
        return " (all tokens considered)";
    }

    private String getTemperatureEffect(Double temp) {
        if (temp <= 0.3)
            return "Model picks most probable tokens → consistent outputs";
        if (temp <= 0.7)
            return "Balanced probability → natural, varied responses";
        if (temp <= 1.0)
            return "Flatter distribution → more creative choices";
        return "Very flat distribution → unexpected, potentially incoherent";
    }

    private String getTopPEffect(Double topP) {
        if (topP <= 0.3)
            return "Only top 30% probability mass → very focused vocabulary";
        if (topP <= 0.7)
            return "Top 70% probability mass → good diversity";
        if (topP <= 0.9)
            return "Top 90% probability mass → broad vocabulary";
        return "All tokens considered → maximum diversity";
    }
}
