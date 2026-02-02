package com.example.ai.basics.day1.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =============================================================================
 * PART 5: EMBEDDINGS - Turning Text into Vectors
 * =============================================================================
 * 
 * LEARNING OBJECTIVES:
 * - Understand what embeddings are
 * - Learn why vectors are powerful for AI
 * - See how similarity search works
 * 
 * WHAT ARE EMBEDDINGS?
 * Embeddings are numerical vector representations of text (or images, audio,
 * etc.).
 * They capture the SEMANTIC MEANING of the content in a format computers can
 * process.
 * 
 * Example: "king" might become [0.2, -0.4, 0.8, 0.1, ...] (hundreds of
 * dimensions)
 * 
 * WHY VECTORS MATTER:
 * 1. Semantic similarity: Similar meanings → similar vectors
 * "happy" and "joyful" have vectors close together
 * "happy" and "sad" have vectors far apart
 * 
 * 2. Mathematical operations:
 * king - man + woman ≈ queen (famous Word2Vec example)
 * 
 * 3. Efficient search: Find similar content using vector distance
 * 
 * HOW SIMILARITY WORKS:
 * - Cosine similarity: Measures the angle between two vectors (-1 to 1)
 * - 1.0 = identical direction (very similar)
 * - 0.0 = orthogonal (unrelated)
 * - -1.0 = opposite direction (opposite meaning)
 * 
 * - Used for: Semantic search, recommendations, clustering, deduplication
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/embeddings")
public class Part5EmbeddingsController {

    /*
     * EmbeddingModel is Spring AI's interface for generating embeddings.
     * It connects to OpenAI's text-embedding-3-small model (configured in
     * properties).
     * 
     * The embedding model is different from the chat model!
     * - Chat model: text in → text out
     * - Embedding model: text in → vector out
     */
    private final EmbeddingModel embeddingModel;

    public Part5EmbeddingsController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * EXERCISE 5.1: Generate an Embedding
     * 
     * See what an embedding looks like for any text.
     * 
     * TRY IT: GET http://localhost:8080/embeddings/generate?text=Hello world
     * 
     * OBSERVE:
     * - The vector has many dimensions (typically 1536 for OpenAI)
     * - Each dimension captures some aspect of meaning
     * - The values are typically between -1 and 1
     */
    @GetMapping("/generate")
    public String generateEmbedding(@RequestParam String text) {
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        float[] embedding = response.getResult().getOutput();

        // Show first 10 and last 5 values (full vector is too large to display)
        StringBuilder vectorPreview = new StringBuilder("[");
        for (int i = 0; i < Math.min(10, embedding.length); i++) {
            vectorPreview.append(String.format("%.6f", embedding[i]));
            if (i < 9)
                vectorPreview.append(", ");
        }
        vectorPreview.append(" ... ");
        for (int i = Math.max(0, embedding.length - 5); i < embedding.length; i++) {
            vectorPreview.append(String.format("%.6f", embedding[i]));
            if (i < embedding.length - 1)
                vectorPreview.append(", ");
        }
        vectorPreview.append("]");

        return String.format("""
                =================================================================
                EMBEDDING GENERATED
                =================================================================

                INPUT TEXT: "%s"

                EMBEDDING VECTOR:
                - Dimensions: %d
                - Preview: %s

                =================================================================
                📚 WHAT THIS MEANS:
                - Each number represents a "feature" of the text's meaning
                - Similar texts will have similar vectors
                - These vectors enable semantic search and comparison

                TECHNICAL DETAILS:
                - Model: text-embedding-3-small
                - Vector size: %d dimensions
                - Each dimension is a floating-point number
                =================================================================
                """, text, embedding.length, vectorPreview, embedding.length);
    }

    /**
     * EXERCISE 5.2: Compare Text Similarity
     * 
     * This is the core of semantic search! Compare how similar two texts are.
     * 
     * TRY THESE:
     * - Similar: http://localhost:8080/embeddings/compare?text1=I love
     * programming&text2=Coding is my passion
     * - Different: http://localhost:8080/embeddings/compare?text1=I love
     * programming&text2=The weather is nice
     * - Opposite: http://localhost:8080/embeddings/compare?text1=I am happy&text2=I
     * am sad
     */
    @GetMapping("/compare")
    public String compareTexts(
            @RequestParam String text1,
            @RequestParam String text2) {

        // Generate embeddings for both texts
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text1, text2));
        float[] embedding1 = response.getResults().get(0).getOutput();
        float[] embedding2 = response.getResults().get(1).getOutput();

        // Calculate cosine similarity
        double similarity = cosineSimilarity(embedding1, embedding2);

        // Interpret the similarity score
        String interpretation = interpretSimilarity(similarity);

        return String.format("""
                =================================================================
                TEXT SIMILARITY COMPARISON
                =================================================================

                TEXT 1: "%s"
                TEXT 2: "%s"

                COSINE SIMILARITY: %.4f

                INTERPRETATION: %s

                =================================================================
                📚 HOW TO READ COSINE SIMILARITY:
                - 0.90 - 1.00: Nearly identical meaning
                - 0.70 - 0.89: Very similar
                - 0.50 - 0.69: Somewhat related
                - 0.30 - 0.49: Loosely related
                - 0.00 - 0.29: Unrelated
                - Negative: Opposite meanings (rare with modern embeddings)

                THIS ENABLES:
                - Semantic search: Find documents by meaning, not keywords
                - Deduplication: Find near-duplicate content
                - Recommendations: "Users who liked X also liked Y"
                =================================================================
                """, text1, text2, similarity, interpretation);
    }

    /**
     * EXERCISE 5.3: Find Most Similar Text
     * 
     * Given a query and multiple candidates, find the most similar.
     * This is how RAG (Retrieval-Augmented Generation) works!
     * 
     * TRY IT:
     * GET http://localhost:8080/embeddings/find-similar?query=How do I cook
     * pasta?&candidates=Boil water and add noodles,The weather is sunny,Italian
     * cuisine uses fresh ingredients,My car needs oil change
     */
    @GetMapping("/find-similar")
    public String findMostSimilar(
            @RequestParam String query,
            @RequestParam List<String> candidates) {

        // Get embedding for the query
        float[] queryEmbedding = embeddingModel.embedForResponse(List.of(query))
                .getResult().getOutput();

        // Get embeddings for all candidates
        EmbeddingResponse candidateResponse = embeddingModel.embedForResponse(candidates);

        // Find the most similar
        StringBuilder result = new StringBuilder();
        result.append("=================================================================\n");
        result.append("FIND MOST SIMILAR TEXT\n");
        result.append("=================================================================\n\n");
        result.append("QUERY: \"").append(query).append("\"\n\n");
        result.append("CANDIDATES (ranked by similarity):\n\n");

        // Calculate similarities and create ranking
        record SimilarityResult(String text, double similarity) {
        }
        List<SimilarityResult> results = new java.util.ArrayList<>();

        for (int i = 0; i < candidates.size(); i++) {
            float[] candidateEmbedding = candidateResponse.getResults().get(i).getOutput();
            double similarity = cosineSimilarity(queryEmbedding, candidateEmbedding);
            results.add(new SimilarityResult(candidates.get(i), similarity));
        }

        // Sort by similarity (highest first)
        results.sort((a, b) -> Double.compare(b.similarity(), a.similarity()));

        int rank = 1;
        for (SimilarityResult r : results) {
            String prefix = rank == 1 ? "🏆 " : "   ";
            result.append(String.format("%s%d. [%.4f] \"%s\"\n",
                    prefix, rank++, r.similarity(), r.text()));
        }

        result.append("""

                =================================================================
                📚 THIS IS THE FOUNDATION OF RAG!

                Retrieval-Augmented Generation (RAG) works by:
                1. Converting your knowledge base to embeddings
                2. When user asks a question, embed the question
                3. Find most similar documents (like we just did!)
                4. Include those documents in the prompt to the LLM
                5. LLM generates answer using the retrieved context

                WHY THIS MATTERS:
                - LLM gets relevant, up-to-date information
                - Reduces hallucination
                - Enables domain-specific knowledge
                =================================================================
                """);

        return result.toString();
    }

    /**
     * Calculate cosine similarity between two vectors.
     * 
     * FORMULA: cos(θ) = (A · B) / (||A|| * ||B||)
     * 
     * Where:
     * - A · B is the dot product
     * - ||A|| and ||B|| are the magnitudes
     */
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

    private String interpretSimilarity(double similarity) {
        if (similarity >= 0.90)
            return "🟢 Nearly identical meaning";
        if (similarity >= 0.70)
            return "🟢 Very similar";
        if (similarity >= 0.50)
            return "🟡 Somewhat related";
        if (similarity >= 0.30)
            return "🟠 Loosely related";
        return "🔴 Unrelated or opposite";
    }
}
