package com.example.ai.basics.day1.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * =============================================================================
 * 🎯 STUDENT TASK: BUILD A SMART MOVIE RECOMMENDATION ENGINE
 * =============================================================================
 * 
 * Welcome to your challenge! You will build a Movie Recommendation Engine that
 * combines ALL the concepts from Parts 2-6:
 * 
 * ✅ Part 2: Basic Prompts (ChatClient, user/system prompts)
 * ✅ Part 3: Prompt Templates (PromptTemplate, few-shot, role-based)
 * ✅ Part 4: Generation Parameters (temperature, creativity control)
 * ✅ Part 5: Embeddings (semantic search, similarity)
 * ✅ Part 6: Context Window (managing context, position effects)
 * 
 * =============================================================================
 * 🎬 THE SCENARIO
 * =============================================================================
 * 
 * You are building a Movie Recommendation Engine for a streaming platform.
 * Users describe what mood they're in or what kind of movie they want,
 * and your system should:
 * 
 * 1. Find semantically similar movies from the "database" (using embeddings)
 * 2. Generate personalized recommendations with proper prompt engineering
 * 3. Allow users to control the creativity level of recommendations
 * 4. Handle conversation history while managing context window limits
 * 
 * =============================================================================
 * 📋 YOUR TASKS
 * =============================================================================
 * 
 * TASK 1: Implement findSimilarMovies() - EMBEDDINGS (Part 5)
 * - Take a user's mood/description and find the most similar movies
 * - Use embeddings and cosine similarity
 * - Return the top N most relevant movies
 * 
 * TASK 2: Implement generateRecommendation() - PROMPT TEMPLATES (Part 3)
 * - Create a well-structured prompt template
 * - Use system prompts for personality
 * - Include few-shot examples for consistent format
 * 
 * TASK 3: Implement generateCreativeRecommendation() - GENERATION PARAMS (Part
 * 4)
 * - Allow user to control temperature (boring=0.2, balanced=0.7, wild=1.3)
 * - Demonstrate how creativity affects recommendations
 * 
 * TASK 4: Implement conversationalRecommendation() - CONTEXT WINDOW (Part 6)
 * - Maintain conversation history
 * - Implement a strategy to handle context limits (summarization/truncation)
 * - Show awareness of "lost in the middle" problem
 * 
 * TASK 5: Implement getFullExperience() - COMBINE ALL (Parts 2-6)
 * - Orchestrate all the above into a complete recommendation flow
 * - This is your capstone showing mastery of all concepts!
 * 
 * =============================================================================
 * 💡 HINTS & TIPS
 * =============================================================================
 * 
 * - Look at the existing Parts 2-6 controllers for examples
 * - Test each task individually before combining
 * - Think about edge cases (empty results, very long inputs, etc.)
 * - Pay attention to the expected output formats
 * 
 * =============================================================================
 * 🏆 GRADING CRITERIA
 * =============================================================================
 * 
 * ⭐ Basic (60%): Tasks 1-2 working correctly
 * ⭐⭐ Good (80%): Tasks 1-3 working, proper error handling
 * ⭐⭐⭐ Excellent (100%): All tasks complete, creative solutions, clean code
 * 🌟 Bonus: Add your own creative feature!
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/movie-engine")
public class Part8StudentTaskController {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    // Simulated movie database with descriptions
    private final List<Movie> movieDatabase;

    // Conversation history storage (in-memory, per session)
    private final Map<String, List<ConversationMessage>> conversationHistory;

    public Part8StudentTaskController(
            ChatClient.Builder chatClientBuilder,
            EmbeddingModel embeddingModel) {
        this.chatClient = chatClientBuilder.build();
        this.embeddingModel = embeddingModel;
        this.movieDatabase = initializeMovieDatabase();
        this.conversationHistory = new ConcurrentHashMap<>();
    }

    // =========================================================================
    // 🎬 MOVIE DATABASE (DO NOT MODIFY)
    // =========================================================================

    private List<Movie> initializeMovieDatabase() {
        return List.of(
                new Movie("The Shawshank Redemption", "Drama",
                        "A story of hope and friendship between two prisoners over decades in a harsh prison. Themes of perseverance, institutional corruption, and the power of hope."),
                new Movie("The Dark Knight", "Action/Thriller",
                        "A masked vigilante battles a chaotic criminal mastermind in a dark city. Explores themes of morality, chaos vs order, and what it means to be a hero."),
                new Movie("Inception", "Sci-Fi/Thriller",
                        "A thief enters people's dreams to steal secrets. Mind-bending journey through layers of reality with themes of grief, memory, and letting go."),
                new Movie("The Grand Budapest Hotel", "Comedy/Drama",
                        "A quirky concierge and his protégé navigate adventures in a colorful European hotel. Whimsical, visually stunning, with themes of friendship and nostalgia."),
                new Movie("Spirited Away", "Animation/Fantasy",
                        "A young girl enters a magical spirit world and must work in a bathhouse for spirits to save her parents. Themes of growing up, identity, and courage."),
                new Movie("Parasite", "Thriller/Drama",
                        "A poor family infiltrates a wealthy household with unexpected consequences. Sharp commentary on class inequality and the desperation of poverty."),
                new Movie("The Matrix", "Sci-Fi/Action",
                        "A hacker discovers reality is a simulation and joins rebels fighting machine overlords. Philosophical themes about free will, reality, and awakening."),
                new Movie("La La Land", "Musical/Romance",
                        "An aspiring actress and a jazz musician fall in love while pursuing their dreams in Los Angeles. Bittersweet story of love, ambition, and sacrifice."),
                new Movie("Interstellar", "Sci-Fi/Drama",
                        "Astronauts travel through a wormhole to find humanity a new home. Epic exploration of love transcending space and time, with scientific grounding."),
                new Movie("Amélie", "Comedy/Romance",
                        "A shy Parisian waitress secretly orchestrates happiness for others around her. Whimsical, colorful, and heartwarming French cinema."),
                new Movie("Get Out", "Horror/Thriller",
                        "A young Black man visits his white girlfriend's family with terrifying results. Social horror exploring racism and cultural appropriation."),
                new Movie("Coco", "Animation/Musical",
                        "A boy enters the Land of the Dead to discover his family's history. Vibrant celebration of Mexican culture, family bonds, and remembrance."),
                new Movie("The Silence of the Lambs", "Horror/Thriller",
                        "An FBI trainee seeks help from an imprisoned cannibalistic serial killer. Psychological tension, brilliant character study, and cat-and-mouse dynamics."),
                new Movie("Eternal Sunshine of the Spotless Mind", "Romance/Sci-Fi",
                        "A couple discovers they erased each other from their memories. Explores love, memory, and whether painful experiences are worth keeping."),
                new Movie("Mad Max: Fury Road", "Action/Sci-Fi",
                        "A woman escapes a tyrannical warlord across a post-apocalyptic wasteland. Non-stop adrenaline, feminist themes, and stunning practical effects."));
    }

    // =========================================================================
    // 📦 HELPER CLASSES (DO NOT MODIFY)
    // =========================================================================

    public record Movie(String title, String genre, String description) {
    }

    public record ScoredMovie(Movie movie, double similarityScore) {
    }

    public record ConversationMessage(String role, String content, long timestamp) {
    }

    // =========================================================================
    // 📊 PROVIDED: Cosine Similarity Function (You can use this directly)
    // =========================================================================

    private double cosineSimilarity(float[] a, float[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // =========================================================================
    // 🔍 TASK 1: FIND SIMILAR MOVIES (Embeddings - Part 5)
    // =========================================================================
    /**
     * Given a user's mood or description, find the most semantically similar
     * movies.
     * 
     * REQUIREMENTS:
     * 1. Generate an embedding for the user's input
     * 2. Compare it with embeddings of all movie descriptions
     * 3. Return the top N movies by similarity score
     * 4. Include similarity scores in the response
     * 
     * EXAMPLE:
     * Input: "I want something mind-bending with philosophical themes"
     * Output: Should return Matrix, Inception, etc.
     * 
     * TEST: GET http://localhost:8080/movie-engine/find-similar?mood=I'm feeling
     * sad and need hope&topN=3
     * 
     * HINT: Look at Part5EmbeddingsController.findMostSimilar() for reference
     */
    @GetMapping("/find-similar")
    public String findSimilarMovies(
            @RequestParam String mood,
            @RequestParam(defaultValue = "3") int topN) {

        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // =====================================================================
        //
        // Step 1: Generate embedding for the user's mood/description
        // - Use embeddingModel.embedForResponse(List.of(mood))
        // - Extract the float[] embedding from the response
        //
        // Step 2: Generate embeddings for all movie descriptions
        // - You can embed all at once for efficiency
        // - Or embed one by one (less efficient but simpler)
        //
        // Step 3: Calculate cosine similarity between mood and each movie
        // - Use the provided cosineSimilarity() method
        // - Store results in a list of ScoredMovie records
        //
        // Step 4: Sort by similarity (highest first) and take top N
        //
        // Step 5: Format and return the results
        // - Include movie title, genre, similarity score, and why it matches
        //
        // =====================================================================

        return """
                ⚠️ TASK 1 NOT IMPLEMENTED

                You need to implement the findSimilarMovies() method!

                Expected behavior:
                - Take user's mood: "%s"
                - Find top %d similar movies using embeddings
                - Return ranked results with similarity scores

                Hint: Look at Part5EmbeddingsController for examples.
                """.formatted(mood, topN);
    }

    // =========================================================================
    // 💬 TASK 2: GENERATE RECOMMENDATION (Prompt Templates - Part 3)
    // =========================================================================
    /**
     * Generate a personalized recommendation based on the user's mood and
     * the movies found.
     * 
     * REQUIREMENTS:
     * 1. Use a PromptTemplate with placeholders
     * 2. Include a system prompt that defines the AI's personality
     * 3. Use few-shot examples for consistent output format
     * 4. The recommendation should feel personal and engaging
     * 
     * OUTPUT FORMAT (enforce this with few-shot examples!):
     * 🎬 MOVIE: [Title]
     * 🎭 GENRE: [Genre]
     * ⭐ MATCH SCORE: [X]%
     * 💡 WHY YOU'LL LOVE IT: [Personalized reason based on user's mood]
     * 🍿 PERFECT FOR: [Occasion/mood]
     * 
     * TEST: GET http://localhost:8080/movie-engine/recommend?mood=I need something
     * uplifting after a hard week
     * 
     * HINT: Look at Part3PromptTemplateController for PromptTemplate usage
     */
    @GetMapping("/recommend")
    public String generateRecommendation(@RequestParam String mood) {

        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // =====================================================================
        //
        // Step 1: First, find similar movies (you can call your Task 1 method
        // or duplicate the logic here)
        //
        // Step 2: Create a PromptTemplate with placeholders like:
        // - {mood} - the user's described mood
        // - {movies} - the top matching movies
        // - {examples} - few-shot examples for format
        //
        // Step 3: Add a system prompt for personality, e.g.:
        // "You are an enthusiastic movie buff who gives personalized
        // recommendations. You're warm, knowledgeable, and always
        // find the perfect movie for someone's mood."
        //
        // Step 4: Include few-shot examples to enforce the output format
        //
        // Step 5: Call the LLM and return the formatted response
        //
        // =====================================================================

        return """
                ⚠️ TASK 2 NOT IMPLEMENTED

                You need to implement the generateRecommendation() method!

                Expected behavior:
                - Take user's mood: "%s"
                - Find relevant movies (use Task 1)
                - Create a PromptTemplate with variables
                - Use system prompt for personality
                - Include few-shot examples for format
                - Return personalized recommendation

                Hint: Look at Part3PromptTemplateController for examples.
                """.formatted(mood);
    }

    // =========================================================================
    // 🎨 TASK 3: CREATIVE RECOMMENDATION (Generation Params - Part 4)
    // =========================================================================
    /**
     * Generate recommendations with user-controlled creativity level.
     * 
     * REQUIREMENTS:
     * 1. Accept a creativityLevel parameter: "safe", "balanced", "wild"
     * 2. Map these to appropriate temperature values
     * 3. Show how different temperatures affect recommendations
     * 4. Clearly label the creativity level in the response
     * 
     * CREATIVITY MAPPING:
     * - "safe" → temperature 0.2 (predictable, mainstream recommendations)
     * - "balanced" → temperature 0.7 (good mix)
     * - "wild" → temperature 1.3 (unexpected, creative connections)
     * 
     * TEST:
     * GET http://localhost:8080/movie-engine/creative-recommend?mood=romantic
     * evening&creativityLevel=safe
     * GET http://localhost:8080/movie-engine/creative-recommend?mood=romantic
     * evening&creativityLevel=wild
     * 
     * Compare the outputs to see how temperature affects recommendations!
     * 
     * HINT: Look at Part4GenerationParamsController for temperature usage
     */
    @GetMapping("/creative-recommend")
    public String generateCreativeRecommendation(
            @RequestParam String mood,
            @RequestParam(defaultValue = "balanced") String creativityLevel) {

        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // =====================================================================
        //
        // Step 1: Map creativityLevel to temperature
        // - "safe" → 0.2
        // - "balanced" → 0.7
        // - "wild" → 1.3
        // - Any other value → default to 0.7
        //
        // Step 2: Build your prompt (can reuse Task 2's template)
        //
        // Step 3: Use OpenAiChatOptions to set the temperature:
        // .options(OpenAiChatOptions.builder()
        // .temperature(temperature)
        // .build())
        //
        // Step 4: Make the LLM call with these options
        //
        // Step 5: Format response to clearly show which creativity level was used
        //
        // =====================================================================

        return """
                ⚠️ TASK 3 NOT IMPLEMENTED

                You need to implement the generateCreativeRecommendation() method!

                Expected behavior:
                - Take user's mood: "%s"
                - Take creativity level: "%s"
                - Map to temperature (safe=0.2, balanced=0.7, wild=1.3)
                - Generate recommendation with that temperature
                - Show how creativity affects the output

                Hint: Look at Part4GenerationParamsController for examples.
                """.formatted(mood, creativityLevel);
    }

    // =========================================================================
    // 🗣️ TASK 4: CONVERSATIONAL MODE (Context Window - Part 6)
    // =========================================================================
    /**
     * Handle multi-turn conversations with context management.
     * 
     * REQUIREMENTS:
     * 1. Maintain conversation history per session (use sessionId)
     * 2. Include relevant history in the prompt
     * 3. Implement a context management strategy:
     * - If history is too long (>10 messages), summarize old messages
     * - Or use a sliding window approach
     * 4. Place important information at the END (avoid "lost in the middle")
     * 5. Allow users to reset their session
     * 
     * TEST (in sequence):
     * GET http://localhost:8080/movie-engine/chat?sessionId=user1&message=I like
     * sci-fi
     * GET http://localhost:8080/movie-engine/chat?sessionId=user1&message=But not
     * too action heavy
     * GET http://localhost:8080/movie-engine/chat?sessionId=user1&message=What do
     * you recommend?
     * GET http://localhost:8080/movie-engine/chat?sessionId=user1&message=reset
     * 
     * HINT: Look at Part6ContextWindowController for context management strategies
     */
    @GetMapping("/chat")
    public String conversationalRecommendation(
            @RequestParam String sessionId,
            @RequestParam String message) {

        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // =====================================================================
        //
        // Step 1: Handle special commands
        // - If message is "reset", clear history and return confirmation
        //
        // Step 2: Retrieve or create conversation history for this session
        // - Use conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>())
        //
        // Step 3: Add the new user message to history
        // - new ConversationMessage("user", message, System.currentTimeMillis())
        //
        // Step 4: Implement context management (IMPORTANT!)
        // - If history.size() > 10, either:
        // a) Summarize older messages into a compact form
        // b) Keep only the last N messages (sliding window)
        // - Remember: Put important context at the END of the prompt
        //
        // Step 5: Build a prompt that includes:
        // - System prompt defining the assistant's role
        // - Conversation history (managed for context limits)
        // - The current user message (at the END!)
        //
        // Step 6: Get the LLM response
        //
        // Step 7: Add assistant response to history
        // - new ConversationMessage("assistant", response, System.currentTimeMillis())
        //
        // Step 8: Return the response with conversation context info
        //
        // =====================================================================

        return """
                ⚠️ TASK 4 NOT IMPLEMENTED

                You need to implement the conversationalRecommendation() method!

                Expected behavior:
                - Session: "%s"
                - Message: "%s"
                - Maintain conversation history
                - Manage context window (summarize or truncate old messages)
                - Put important info at the END (avoid "lost in the middle")

                Hint: Look at Part6ContextWindowController for examples.
                """.formatted(sessionId, message);
    }

    // =========================================================================
    // 🌟 TASK 5: FULL EXPERIENCE (Combine All Parts 2-6!)
    // =========================================================================
    /**
     * The capstone task: Combine EVERYTHING into one seamless experience!
     * 
     * REQUIREMENTS:
     * 1. Find similar movies using embeddings (Part 5)
     * 2. Use prompt templates for structured output (Part 3)
     * 3. Allow creativity control (Part 4)
     * 4. Consider context management for large outputs (Part 6)
     * 5. Demonstrate proper use of system/user prompts (Part 2)
     * 
     * The response should include:
     * - TOP 3 recommendations with similarity scores
     * - Personalized explanations for each
     * - A "mood analysis" of the user's request
     * - A "perfect match" highlight for the best movie
     * - Fun facts about one of the recommended movies
     * 
     * TEST: GET http://localhost:8080/movie-engine/full-experience?mood=I want a
     * visually stunning movie with deep themes about
     * identity&creativityLevel=balanced
     * 
     * THIS IS YOUR CAPSTONE - SHOW OFF YOUR SKILLS! 🚀
     */
    @GetMapping("/full-experience")
    public String getFullExperience(
            @RequestParam String mood,
            @RequestParam(defaultValue = "balanced") String creativityLevel) {

        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // =====================================================================
        //
        // This is your chance to combine everything!
        //
        // Suggested approach:
        //
        // 1. EMBEDDINGS (Part 5)
        // - Find top 3 similar movies based on mood
        // - Store with similarity scores
        //
        // 2. MOOD ANALYSIS (Part 2 - Basic Prompts)
        // - Use a simple prompt to analyze the user's mood
        // - What emotions? What are they looking for?
        //
        // 3. PROMPT TEMPLATE (Part 3)
        // - Create a comprehensive template that includes:
        // * The matched movies
        // * The mood analysis
        // * Few-shot examples for format
        //
        // 4. CREATIVITY CONTROL (Part 4)
        // - Apply the appropriate temperature
        //
        // 5. CONTEXT MANAGEMENT (Part 6)
        // - Ensure your prompt isn't too long
        // - Put the most important info (movies, mood) at the end
        //
        // 6. FINAL OUTPUT
        // - Beautiful, formatted response
        // - Clear sections
        // - Personalized feel
        //
        // =====================================================================

        return """
                ⚠️ TASK 5 NOT IMPLEMENTED

                You need to implement the getFullExperience() method!

                This is the CAPSTONE TASK - combine ALL concepts:
                - Mood: "%s"
                - Creativity: "%s"

                Requirements:
                ✅ Use embeddings to find similar movies (Part 5)
                ✅ Use prompt templates for structure (Part 3)
                ✅ Apply creativity/temperature control (Part 4)
                ✅ Manage context appropriately (Part 6)
                ✅ Use proper system/user prompts (Part 2)

                Show off your skills! 🚀
                """.formatted(mood, creativityLevel);
    }

    // =========================================================================
    // 📋 BONUS: HEALTH CHECK (already implemented for you)
    // =========================================================================

    @GetMapping("/health")
    public String healthCheck() {
        return """
                =================================================================
                🎬 MOVIE RECOMMENDATION ENGINE - STATUS CHECK
                =================================================================

                ✅ Server is running!
                ✅ Movie database loaded: %d movies
                ✅ Active sessions: %d

                Available endpoints to test your implementation:

                🔍 TASK 1: Find Similar Movies
                   GET /movie-engine/find-similar?mood=YOUR_MOOD&topN=3

                💬 TASK 2: Generate Recommendation
                   GET /movie-engine/recommend?mood=YOUR_MOOD

                🎨 TASK 3: Creative Recommendation
                   GET /movie-engine/creative-recommend?mood=YOUR_MOOD&creativityLevel=safe|balanced|wild

                🗣️ TASK 4: Conversational Mode
                   GET /movie-engine/chat?sessionId=YOUR_ID&message=YOUR_MESSAGE

                🌟 TASK 5: Full Experience (Capstone)
                   GET /movie-engine/full-experience?mood=YOUR_MOOD&creativityLevel=balanced

                =================================================================
                GOOD LUCK! Remember to apply all concepts from Parts 2-6! 🍿
                =================================================================
                """.formatted(movieDatabase.size(), conversationHistory.size());
    }

    @GetMapping("/movies")
    public String listMovies() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("🎬 MOVIE DATABASE (%d movies)\n".formatted(movieDatabase.size()));
        sb.append("=================================================================\n\n");

        for (int i = 0; i < movieDatabase.size(); i++) {
            Movie m = movieDatabase.get(i);
            sb.append("%02d. %s (%s)\n".formatted(i + 1, m.title(), m.genre()));
            sb.append("    %s\n\n".formatted(m.description()));
        }

        return sb.toString();
    }
}
