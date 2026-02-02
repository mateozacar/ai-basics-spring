package com.example.ai.basics.day1.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * =============================================================================
 * SIMPLE IN-MEMORY VECTOR STORE
 * =============================================================================
 * 
 * This service stores text embeddings in memory for similarity search.
 * In production, you would use a vector database like:
 * - Pinecone
 * - Weaviate
 * - Milvus
 * - PostgreSQL with pgvector
 * - Redis with vector search
 * 
 * WHAT THIS DEMONSTRATES:
 * - How embeddings are stored with their source text
 * - How similarity search works at a basic level
 * - The foundation for RAG (Retrieval-Augmented Generation)
 * 
 * =============================================================================
 */
@Service
public class SimpleVectorStore {

    private final EmbeddingModel embeddingModel;

    // In-memory storage: Maps document ID to (text, embedding) pairs
    private final Map<String, StoredDocument> documents = new HashMap<>();

    public SimpleVectorStore(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * Stores a document with its embedding.
     * 
     * @param id   Unique identifier for the document
     * @param text The text content to store
     */
    public void store(String id, String text) {
        // Generate embedding for the text
        float[] embedding = embeddingModel.embedForResponse(List.of(text))
                .getResult().getOutput();

        documents.put(id, new StoredDocument(id, text, embedding));
    }

    /**
     * Stores multiple documents.
     */
    public void storeAll(Map<String, String> textByIds) {
        textByIds.forEach(this::store);
    }

    /**
     * Finds the most similar documents to the query.
     * 
     * @param query The search query
     * @param topK  Number of results to return
     * @return List of similar documents, ranked by similarity
     */
    public List<SimilarityResult> findSimilar(String query, int topK) {
        if (documents.isEmpty()) {
            return Collections.emptyList();
        }

        // Generate embedding for the query
        float[] queryEmbedding = embeddingModel.embedForResponse(List.of(query))
                .getResult().getOutput();

        // Calculate similarity with all stored documents
        List<SimilarityResult> results = new ArrayList<>();
        for (StoredDocument doc : documents.values()) {
            double similarity = cosineSimilarity(queryEmbedding, doc.embedding());
            results.add(new SimilarityResult(doc.id(), doc.text(), similarity));
        }

        // Sort by similarity (highest first) and return top K
        results.sort((a, b) -> Double.compare(b.similarity(), a.similarity()));
        return results.subList(0, Math.min(topK, results.size()));
    }

    /**
     * Clears all stored documents.
     */
    public void clear() {
        documents.clear();
    }

    /**
     * Returns the number of stored documents.
     */
    public int size() {
        return documents.size();
    }

    /**
     * Calculate cosine similarity between two vectors.
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

    // Record classes for data storage
    public record StoredDocument(String id, String text, float[] embedding) {
    }

    public record SimilarityResult(String id, String text, double similarity) {
    }
}
