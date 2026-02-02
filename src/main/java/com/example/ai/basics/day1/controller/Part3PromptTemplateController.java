package com.example.ai.basics.day1.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * =============================================================================
 * PART 3: PROMPT TEMPLATES - Mastering Prompt Engineering
 * =============================================================================
 * 
 * LEARNING OBJECTIVES:
 * - Understand why prompt templates are essential
 * - Learn prompt engineering best practices
 * - See how structured prompts improve output quality
 * 
 * WHY PROMPT TEMPLATES?
 * 1. Reusability: Same structure, different inputs
 * 2. Consistency: Predictable output format
 * 3. Maintainability: Easy to update and version control
 * 4. Prompt Engineering: Apply proven patterns
 * 
 * PROMPT ENGINEERING PRINCIPLES:
 * 1. Be specific and clear
 * 2. Provide context
 * 3. Define the output format
 * 4. Use examples (few-shot learning)
 * 5. Set constraints (length, style, etc.)
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/prompt-templates")
public class Part3PromptTemplateController {

    private final ChatClient chatClient;

    public Part3PromptTemplateController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * EXERCISE 3.1: BAD vs GOOD Prompt Comparison
     * 
     * This endpoint demonstrates how prompt quality affects output.
     * 
     * TRY IT: GET http://localhost:8080/prompt-templates/compare?topic=recursion
     * 
     * BAD PROMPT ISSUES:
     * - Vague instruction ("explain")
     * - No target audience
     * - No format specification
     * - No length constraints
     * 
     * GOOD PROMPT IMPROVEMENTS:
     * - Specific role (teacher)
     * - Clear audience (beginners)
     * - Defined structure (parts)
     * - Length constraint (3 sentences)
     * - Example request
     */
    @GetMapping("/compare")
    public String compareBadAndGoodPrompt(@RequestParam String topic) {
        // ❌ BAD PROMPT: Vague, no structure, no constraints
        String badPrompt = "explain " + topic;

        // ✅ GOOD PROMPT: Specific, structured, constrained
        String goodPrompt = String.format("""
                You are an expert programming teacher.

                Explain "%s" to a beginner programmer.

                Structure your response as:
                1. One-sentence definition
                2. Real-world analogy
                3. Simple code example

                Keep each section to 2-3 sentences maximum.
                """, topic);

        String badResponse = chatClient.prompt()
                .user(badPrompt)
                .call()
                .content();

        String goodResponse = chatClient.prompt()
                .user(goodPrompt)
                .call()
                .content();

        return String.format("""
                =================================================================
                🔴 BAD PROMPT EXAMPLE
                =================================================================
                Prompt: "%s"

                Response:
                %s

                =================================================================
                🟢 GOOD PROMPT EXAMPLE
                =================================================================
                Prompt:
                %s

                Response:
                %s

                =================================================================
                📚 KEY LESSONS:
                - Specificity > Vagueness
                - Structure guides the response
                - Constraints prevent rambling
                - Context (role, audience) shapes the tone
                =================================================================
                """, badPrompt, badResponse, goodPrompt, goodResponse);
    }

    /**
     * EXERCISE 3.2: Using Spring AI's PromptTemplate
     * 
     * Spring AI provides PromptTemplate for dynamic prompt generation.
     * Variables are defined with {placeholder} syntax.
     * 
     * TRY IT:
     * GET
     * http://localhost:8080/prompt-templates/explain?topic=APIs&audience=child&style=playful
     */
    @GetMapping("/explain")
    public String explainWithTemplate(
            @RequestParam String topic,
            @RequestParam(defaultValue = "beginner") String audience,
            @RequestParam(defaultValue = "friendly") String style) {

        // Define the template with placeholders
        String templateString = """
                You are a {style} teacher explaining concepts to a {audience}.

                Explain "{topic}" in a way that is:
                - Easy to understand for your audience
                - Engaging and memorable
                - Practical with real examples

                Include one analogy and one practical tip.
                Keep your response under 200 words.
                """;

        // Create PromptTemplate and fill in variables
        PromptTemplate template = new PromptTemplate(templateString);
        String filledPrompt = template.render(Map.of(
                "topic", topic,
                "audience", audience,
                "style", style));

        String response = chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();

        return String.format("""
                TEMPLATE VARIABLES:
                - topic: %s
                - audience: %s
                - style: %s

                GENERATED PROMPT:
                %s

                RESPONSE:
                %s
                """, topic, audience, style, filledPrompt, response);
    }

    /**
     * EXERCISE 3.3: Role-Based Prompting
     * 
     * Different roles produce different response styles.
     * This is a powerful prompt engineering technique!
     * 
     * TRY IT: Try different roles:
     * GET http://localhost:8080/prompt-templates/role?role=pirate&question=What is
     * cloud computing?
     * GET http://localhost:8080/prompt-templates/role?role=scientist&question=What
     * is cloud computing?
     * GET http://localhost:8080/prompt-templates/role?role=chef&question=What is
     * cloud computing?
     */
    @GetMapping("/role")
    public String roleBasedPrompt(
            @RequestParam String role,
            @RequestParam String question) {

        return chatClient.prompt()
                .system(String.format("""
                        You are a %s. Answer questions in character, using
                        vocabulary and metaphors from your profession/role.
                        Be creative but still informative.
                        Keep responses to 3-4 sentences.
                        """, role))
                .user(question)
                .call()
                .content();
    }

    /**
     * EXERCISE 3.4: Few-Shot Learning with Examples
     * 
     * Providing examples in the prompt helps the LLM understand
     * the exact format and style you want. This is called "few-shot learning."
     * 
     * TRY IT: GET http://localhost:8080/prompt-templates/few-shot?word=serendipity
     */
    @GetMapping("/few-shot")
    public String fewShotLearning(@RequestParam String word) {
        String fewShotPrompt = String.format("""
                Create a definition in my specific format. Follow these examples EXACTLY:

                EXAMPLE 1:
                Word: ephemeral
                Definition: lasting for a very short time
                Usage: The ephemeral beauty of cherry blossoms reminds us to appreciate the moment.
                Fun fact: From Greek "ephemeros" meaning "lasting only a day"

                EXAMPLE 2:
                Word: ubiquitous
                Definition: present everywhere at the same time
                Usage: Smartphones have become ubiquitous in modern society.
                Fun fact: Comes from Latin "ubique" meaning "everywhere"

                Now do this for:
                Word: %s
                """, word);

        return chatClient.prompt()
                .user(fewShotPrompt)
                .call()
                .content();
    }

    /**
     * EXERCISE 3.5: Chain of Thought Prompting
     * 
     * Asking the LLM to "think step by step" improves reasoning.
     * This is especially useful for complex problems.
     * 
     * TRY IT: GET http://localhost:8080/prompt-templates/chain-of-thought?problem=A
     * store sells apples for $2 each. If I buy 3 apples and give the cashier $20,
     * how much change do I get?
     */
    @GetMapping("/chain-of-thought")
    public String chainOfThought(@RequestParam String problem) {
        String cotPrompt = String.format("""
                Solve this problem step by step.

                Problem: %s

                Think through this carefully:
                1. First, identify what information is given
                2. Then, determine what we need to find
                3. Show each calculation step
                4. Finally, state the answer clearly

                Let's solve this step by step:
                """, problem);

        return chatClient.prompt()
                .user(cotPrompt)
                .call()
                .content();
    }
}
