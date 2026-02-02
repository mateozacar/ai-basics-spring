# 🎬 Student Task: Build a Smart Movie Recommendation Engine

<p align="center">
  <img src="https://img.shields.io/badge/Difficulty-Intermediate%20to%20Advanced-orange" />
  <img src="https://img.shields.io/badge/Parts%20Covered-2%20through%206-blue" />
  <img src="https://img.shields.io/badge/Estimated%20Time-2--3%20Hours-green" />
</p>

---

## 📋 Overview

In this challenge, you will build a **Smart Movie Recommendation Engine** that combines ALL the AI concepts you've learned in Parts 2-6. This is a hands-on integration task where you'll implement a mini RAG (Retrieval-Augmented Generation) system with proper prompt engineering.

### 🎯 What You'll Build

A movie recommendation system that:
1. **Understands user mood** using semantic search (embeddings)
2. **Generates personalized recommendations** using prompt templates
3. **Controls creativity level** with temperature settings
4. **Maintains conversations** while managing context window limits

---

## 🧠 Concepts Covered

| Part | Concept | How It's Used |
|------|---------|---------------|
| **Part 2** | Basic Prompts | System/user prompts, ChatClient |
| **Part 3** | Prompt Templates | PromptTemplate, few-shot learning, role-based prompting |
| **Part 4** | Generation Parameters | Temperature control for creativity |
| **Part 5** | Embeddings | Finding semantically similar movies |
| **Part 6** | Context Window | Managing conversation history |

---

## 🎮 The Tasks

### 📍 TASK 1: Find Similar Movies (Embeddings)
**File:** `Part8StudentTaskController.java` → `findSimilarMovies()`

**Endpoint:** `GET /movie-engine/find-similar?mood=YOUR_MOOD&topN=3`

**What to implement:**
- Generate an embedding for the user's mood description
- Compare with embeddings of all movie descriptions
- Calculate cosine similarity (use the provided `cosineSimilarity()` method)
- Return the top N most similar movies with their scores

**Test it:**
```bash
curl "http://localhost:8080/movie-engine/find-similar?mood=I%20want%20something%20mind-bending%20with%20philosophical%20themes&topN=3"
```

**Hint:** Look at `Part5EmbeddingsController.findMostSimilar()` for reference.

---

### 📍 TASK 2: Generate Recommendation (Prompt Templates)
**File:** `Part8StudentTaskController.java` → `generateRecommendation()`

**Endpoint:** `GET /movie-engine/recommend?mood=YOUR_MOOD`

**What to implement:**
- Use Task 1 to find relevant movies
- Create a `PromptTemplate` with placeholders
- Add a system prompt for personality (movie buff character)
- Include few-shot examples for consistent output format

**Required Output Format:**
```
🎬 MOVIE: [Title]
🎭 GENRE: [Genre]  
⭐ MATCH SCORE: [X]%
💡 WHY YOU'LL LOVE IT: [Personalized reason]
🍿 PERFECT FOR: [Occasion/mood]
```

**Test it:**
```bash
curl "http://localhost:8080/movie-engine/recommend?mood=I%20need%20something%20uplifting"
```

**Hint:** Look at `Part3PromptTemplateController` for PromptTemplate usage.

---

### 📍 TASK 3: Creative Recommendation (Temperature Control)
**File:** `Part8StudentTaskController.java` → `generateCreativeRecommendation()`

**Endpoint:** `GET /movie-engine/creative-recommend?mood=YOUR_MOOD&creativityLevel=safe|balanced|wild`

**What to implement:**
- Map creativity levels to temperature values:
  - `"safe"` → temperature `0.2` (predictable)
  - `"balanced"` → temperature `0.7` (good mix)
  - `"wild"` → temperature `1.3` (unexpected)
- Use `OpenAiChatOptions.builder().temperature(temp).build()`
- Show how creativity affects recommendations

**Test it:**
```bash
# Compare these two:
curl "http://localhost:8080/movie-engine/creative-recommend?mood=romantic%20evening&creativityLevel=safe"
curl "http://localhost:8080/movie-engine/creative-recommend?mood=romantic%20evening&creativityLevel=wild"
```

**Hint:** Look at `Part4GenerationParamsController` for temperature usage.

---

### 📍 TASK 4: Conversational Mode (Context Window)
**File:** `Part8StudentTaskController.java` → `conversationalRecommendation()`

**Endpoint:** `GET /movie-engine/chat?sessionId=YOUR_ID&message=YOUR_MESSAGE`

**What to implement:**
- Maintain conversation history per session
- Handle the "reset" command to clear history
- Implement context management:
  - If history > 10 messages, summarize or truncate
  - Remember "lost in the middle" - put important info at END
- Build prompts that include conversation context

**Test it (in sequence):**
```bash
curl "http://localhost:8080/movie-engine/chat?sessionId=user1&message=I%20like%20sci-fi"
curl "http://localhost:8080/movie-engine/chat?sessionId=user1&message=But%20not%20too%20action%20heavy"  
curl "http://localhost:8080/movie-engine/chat?sessionId=user1&message=What%20do%20you%20recommend"
curl "http://localhost:8080/movie-engine/chat?sessionId=user1&message=reset"
```

**Hint:** Look at `Part6ContextWindowController` for context strategies.

---

### 📍 TASK 5: Full Experience (Capstone - Combine All!)
**File:** `Part8StudentTaskController.java` → `getFullExperience()`

**Endpoint:** `GET /movie-engine/full-experience?mood=YOUR_MOOD&creativityLevel=balanced`

**What to implement:**
Combine ALL concepts into one seamless experience:

1. ✅ Use **embeddings** to find similar movies (Part 5)
2. ✅ Analyze the user's mood with a **basic prompt** (Part 2)
3. ✅ Use **prompt templates** for structured output (Part 3)
4. ✅ Apply **temperature control** based on creativity level (Part 4)
5. ✅ **Manage context** appropriately (Part 6)

**Required output should include:**
- TOP 3 recommendations with similarity scores
- Personalized explanations for each
- A "mood analysis" of the user's request
- A "perfect match" highlight
- Fun facts about one recommended movie

**Test it:**
```bash
curl "http://localhost:8080/movie-engine/full-experience?mood=I%20want%20visually%20stunning%20with%20deep%20themes&creativityLevel=balanced"
```

---

## 📊 Grading Rubric

| Level | Requirements | Points |
|-------|--------------|--------|
| ⭐ **Basic** | Tasks 1-2 working correctly | 60% |
| ⭐⭐ **Good** | Tasks 1-3 working, proper error handling | 80% |
| ⭐⭐⭐ **Excellent** | All 5 tasks complete, clean code, creative solutions | 100% |
| 🌟 **Bonus** | Add your own creative feature (+10%) | 110% |

### Bonus Ideas:
- Add a `/movie-engine/compare` endpoint that lets users compare two movies
- Implement movie filtering by genre
- Add a "mood history" feature that tracks what moods a user has searched for
- Create a "surprise me" endpoint with very high temperature

---

## 🛠️ Getting Started

### 1. Check Available Movies
```bash
curl http://localhost:8080/movie-engine/movies
```

### 2. Check Health Status
```bash
curl http://localhost:8080/movie-engine/health
```

### 3. Reference Files
- `Part2BasicPromptController.java` - Basic prompt examples
- `Part3PromptTemplateController.java` - Template examples
- `Part4GenerationParamsController.java` - Temperature examples
- `Part5EmbeddingsController.java` - Embedding examples
- `Part6ContextWindowController.java` - Context examples

---

## 💡 Tips for Success

1. **Start with Task 1** - It's the foundation for other tasks
2. **Test incrementally** - Don't try to implement everything at once
3. **Use the provided helper methods** - `cosineSimilarity()` is already there
4. **Look at the reference files** - Parts 2-6 have working examples
5. **Think about edge cases** - What if no movies match? What if the input is very long?

---

## 🚀 Submission

1. Complete all TODO sections in `Part8StudentTaskController.java`
2. Test all endpoints to ensure they work
3. Document any bonus features you added
4. Submit your completed controller file

---

Good luck! 🍿🎬
