package com.example.ai.basics.day1.controller;

import com.example.ai.basics.day1.service.SimpleVectorStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * =============================================================================
 * PART 7: SMART STUDY ASSISTANT - Integrated Exercise
 * =============================================================================
 * 
 * LEARNING OBJECTIVES:
 * - Combine all GenAI concepts in a realistic application
 * - Understand how RAG (Retrieval-Augmented Generation) works
 * - See how embeddings + LLM create intelligent assistants
 * 
 * WHAT THIS APPLICATION DOES:
 * 1. Accept study notes from users (stores as embeddings)
 * 2. Accept questions about the notes
 * 3. Find relevant notes using similarity search
 * 4. Generate answers using LLM with retrieved context
 * 
 * GENAI CONCEPTS DEMONSTRATED:
 * - Embeddings: Notes are converted to vectors
 * - Similarity Search: Finding relevant content
 * - Prompt Templates: Structured prompts for Q&A
 * - Temperature Control: Adjustable creativity
 * - Context Window: Managing what the LLM sees
 * 
 * HOW THIS RESEMBLES PRODUCTION RAG:
 * This is a simplified but complete RAG pipeline!
 * 
 * R - Retrieval: Find relevant documents using embeddings
 * A - Augmented: Add retrieved content to the prompt
 * G - Generation: LLM generates answer using context
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/study-assistant")
public class Part7StudyAssistantController {

    private final ChatClient chatClient;
    private final SimpleVectorStore vectorStore;

    public Part7StudyAssistantController(
            ChatClient.Builder chatClientBuilder,
            SimpleVectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    /**
     * EXERCISE 7.1: Add Study Notes
     * 
     * Upload notes to the study assistant. These get converted to embeddings
     * and stored for later retrieval.
     * 
     * TRY IT:
     * POST http://localhost:8080/study-assistant/notes
     * Content-Type: application/json
     * 
     * {
     * "notes": [
     * "Photosynthesis is the process by which plants convert sunlight, water, and
     * CO2 into glucose and oxygen. It occurs in the chloroplasts.",
     * "The mitochondria is the powerhouse of the cell. It produces ATP through
     * cellular respiration.",
     * "DNA stands for deoxyribonucleic acid. It contains the genetic instructions
     * for all living organisms.",
     * "The water cycle includes evaporation, condensation, precipitation, and
     * collection."
     * ]
     * }
     */
    @PostMapping("/notes")
    public String addNotes(@RequestBody NotesRequest request) {
        List<String> notes = request.notes();

        // Store each note with a unique ID
        for (String note : notes) {
            String id = UUID.randomUUID().toString();
            vectorStore.store(id, note);
        }

        return String.format("""
                =================================================================
                ✅ NOTES ADDED SUCCESSFULLY
                =================================================================

                Added %d notes to the study assistant.

                WHAT HAPPENED BEHIND THE SCENES:
                1. Each note was sent to the embedding model
                2. The model converted text → vector (embedding)
                3. The embedding was stored in our vector store

                Total notes in knowledge base: %d

                =================================================================
                NEXT STEP: Ask a question about your notes!

                POST /study-assistant/ask
                {
                  "question": "What is photosynthesis?"
                }
                =================================================================
                """, notes.size(), vectorStore.size());
    }

    /**
     * EXERCISE 7.2: Ask a Question (Basic)
     * 
     * Ask a question and get an answer based on stored notes.
     * 
     * TRY IT:
     * POST http://localhost:8080/study-assistant/ask
     * Content-Type: application/json
     * 
     * {
     * "question": "What produces energy in cells?"
     * }
     */
    @PostMapping("/ask")
    public String askQuestion(@RequestBody QuestionRequest request) {
        String question = request.question();
        int topK = request.topK() != null ? request.topK() : 2;
        Double temperature = request.temperature() != null ? request.temperature() : 0.7;

        // Step 1: Find relevant notes using similarity search
        List<SimpleVectorStore.SimilarityResult> relevantNotes = vectorStore.findSimilar(question, topK);

        if (relevantNotes.isEmpty()) {
            return "❌ No notes found! Please add some notes first using POST /study-assistant/notes";
        }

        // Step 2: Build context from retrieved notes
        String context = relevantNotes.stream()
                .map(r -> "- " + r.text())
                .collect(Collectors.joining("\n"));

        // Step 3: Create the RAG prompt
        String ragPromptTemplate = """
                You are a helpful study assistant. Answer the student's question
                based ONLY on the provided context. If the context doesn't contain
                enough information, say so honestly.

                CONTEXT (from study notes):
                {context}

                STUDENT'S QUESTION:
                {question}

                Provide a clear, educational answer. If helpful, suggest what else
                the student might want to learn about this topic.
                """;

        PromptTemplate template = new PromptTemplate(ragPromptTemplate);
        String filledPrompt = template.render(Map.of(
                "context", context,
                "question", question));

        // Step 4: Generate answer with the LLM
        String answer = chatClient.prompt()
                .user(filledPrompt)
                .options(OpenAiChatOptions.builder()
                        .temperature(temperature)
                        .build())
                .call()
                .content();

        // Format detailed response for learning
        StringBuilder retrievalDetails = new StringBuilder();
        for (SimpleVectorStore.SimilarityResult note : relevantNotes) {
            retrievalDetails.append(String.format("  [%.4f] %s\n",
                    note.similarity(),
                    note.text().length() > 80
                            ? note.text().substring(0, 80) + "..."
                            : note.text()));
        }

        return String.format("""
                =================================================================
                🎓 SMART STUDY ASSISTANT - ANSWER
                =================================================================

                📝 YOUR QUESTION: "%s"

                🔍 STEP 1 - RETRIEVAL (Finding relevant notes):
                Found %d relevant notes (similarity scores):
                %s

                📄 STEP 2 - CONTEXT (What the LLM sees):
                %s

                🤖 STEP 3 - GENERATION (LLM Answer):
                %s

                =================================================================
                ⚙️ PARAMETERS USED:
                - Top-K retrieved: %d
                - Temperature: %.2f

                📚 THIS IS RAG IN ACTION!
                R - Retrieved %d relevant notes using embeddings
                A - Augmented the prompt with retrieved context
                G - Generated an answer grounded in your actual notes

                🔧 TRY DIFFERENT SETTINGS:
                - Higher topK = More context (but may include less relevant notes)
                - Lower temperature = More factual answers
                - Higher temperature = More creative explanations
                =================================================================
                """, question, relevantNotes.size(), retrievalDetails, context,
                answer, topK, temperature, relevantNotes.size());
    }

    /**
     * EXERCISE 7.3: Compare With and Without RAG
     * 
     * This demonstrates why RAG is valuable - compare answers with and
     * without the retrieved context.
     * 
     * TRY IT:
     * POST http://localhost:8080/study-assistant/compare-rag
     * Content-Type: application/json
     * 
     * {
     * "question": "What is photosynthesis?",
     * "customContext": "In our class, we learned that photosynthesis happens in the
     * thylakoid membranes and uses light-dependent and light-independent
     * reactions."
     * }
     */
    @PostMapping("/compare-rag")
    public String compareWithAndWithoutRag(@RequestBody CompareRequest request) {
        String question = request.question();
        String customContext = request.customContext();

        // Get retrieved context if available
        List<SimpleVectorStore.SimilarityResult> retrieved = vectorStore.findSimilar(question, 2);
        String retrievedContext = retrieved.stream()
                .map(r -> "- " + r.text())
                .collect(Collectors.joining("\n"));

        // Answer WITHOUT context (just the LLM's training knowledge)
        String noRagPrompt = String.format("""
                Answer this question concisely: %s
                """, question);

        String noRagAnswer = chatClient.prompt()
                .user(noRagPrompt)
                .call()
                .content();

        // Answer WITH retrieved context (RAG)
        String ragPrompt = String.format("""
                Based ONLY on this context, answer the question.

                Context:
                %s
                %s

                Question: %s
                """,
                retrievedContext.isEmpty() ? "(No stored notes found)" : retrievedContext,
                customContext != null ? "\nAdditional context: " + customContext : "",
                question);

        String ragAnswer = chatClient.prompt()
                .user(ragPrompt)
                .call()
                .content();

        return String.format("""
                =================================================================
                🔄 RAG COMPARISON: With vs Without Context
                =================================================================

                📝 QUESTION: "%s"

                -----------------------------------------------------------------
                ❌ WITHOUT RAG (Just LLM's Training Data):
                -----------------------------------------------------------------
                %s

                -----------------------------------------------------------------
                ✅ WITH RAG (Using Your Notes + Custom Context):
                -----------------------------------------------------------------
                Retrieved Context:
                %s
                %s

                Answer:
                %s

                =================================================================
                📚 KEY INSIGHTS:

                WITHOUT RAG:
                - Uses only the LLM's training data
                - May be outdated or generic
                - Not personalized to your study material

                WITH RAG:
                - Uses YOUR specific notes and context
                - More relevant and accurate for your studies
                - Can include information the LLM wasn't trained on

                THIS IS WHY RAG MATTERS:
                - 🎯 More accurate answers for your specific domain
                - 📅 Can include up-to-date information
                - 🔒 Reduces hallucination by grounding in facts
                - 📚 Personalizes the AI to your knowledge base
                =================================================================
                """, question, noRagAnswer,
                retrievedContext.isEmpty() ? "(No stored notes)" : retrievedContext,
                customContext != null ? "\nCustom: " + customContext : "",
                ragAnswer);
    }

    /**
     * EXERCISE 7.4: Clear All Notes
     * 
     * Reset the knowledge base.
     * 
     * TRY IT: DELETE http://localhost:8080/study-assistant/notes
     */
    @DeleteMapping("/notes")
    public String clearNotes() {
        int count = vectorStore.size();
        vectorStore.clear();

        return String.format("""
                =================================================================
                🗑️ KNOWLEDGE BASE CLEARED
                =================================================================

                Removed %d notes from the study assistant.

                The vector store is now empty. Add new notes to start fresh!
                =================================================================
                """, count);
    }

    /**
     * EXERCISE 7.5: Get Status
     * 
     * Check how many notes are stored.
     * 
     * TRY IT: GET http://localhost:8080/study-assistant/status
     */
    @GetMapping("/status")
    public String getStatus() {
        return String.format("""
                =================================================================
                📊 STUDY ASSISTANT STATUS
                =================================================================

                Notes in knowledge base: %d

                =================================================================
                AVAILABLE ENDPOINTS:

                POST /study-assistant/notes
                  - Add new study notes

                POST /study-assistant/ask
                  - Ask a question (with optional topK and temperature)

                POST /study-assistant/compare-rag
                  - Compare answers with and without RAG

                DELETE /study-assistant/notes
                  - Clear all notes

                GET /study-assistant/status
                  - This status page
                =================================================================
                """, vectorStore.size());
    }

    // Request/Response records
    public record NotesRequest(List<String> notes) {
    }

    public record QuestionRequest(String question, Integer topK, Double temperature) {
    }

    public record CompareRequest(String question, String customContext) {
    }
}
