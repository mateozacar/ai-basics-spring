# 🎓 AI Basics - Spring AI Educational Application

> A hands-on learning application demonstrating **Generative AI fundamentals** using Spring Boot and Spring AI.

## 📚 What You'll Learn

This application is designed to teach key GenAI concepts through practical, runnable examples:

| Part | Topic | Endpoints |
|------|-------|-----------|
| 1 | Project Setup | Configuration in `application.properties` |
| 2 | Basic Prompts | `/basic-prompt/*` |
| 3 | Prompt Templates | `/prompt-templates/*` |
| 4 | Generation Parameters | `/generation-params/*` |
| 5 | Embeddings | `/embeddings/*` |
| 6 | Context Window | `/context-window/*` |
| 7 | Study Assistant (RAG) | `/study-assistant/*` |
| **8** | **🎯 Student Task: Movie Engine** | `/movie-engine/*` |

---

## 🎯 Parts Overview

### Part 1: Project Setup
Configuration files that connect your app to the LLM.

| File | What It Teaches |
|------|-----------------|
| `build.gradle` | Spring AI dependencies, BOM management |
| `application.properties` | API key, model selection, temperature, top-p, max tokens |

---

### Part 2: Basic Prompts (`/basic-prompt/*`)
Your first interactions with an LLM.

| Endpoint | Method | What It Teaches |
|----------|--------|-----------------|
| `/hello` | GET | First LLM interaction, fixed prompt |
| `/ask?prompt=...` | GET | Custom user prompts |
| `/with-system?prompt=...` | GET | System prompts (setting LLM personality) |
| `/token-demo` | GET | Token estimation, response variability |

**Key Concepts:** Prompts, Tokens, Context Window, System vs User Messages

---

### Part 3: Prompt Templates (`/prompt-templates/*`)
Master the art of prompt engineering.

| Endpoint | Method | What It Teaches |
|----------|--------|-----------------|
| `/compare?topic=...` | GET | Bad vs Good prompts comparison |
| `/explain?topic=...&audience=...&style=...` | GET | Spring AI PromptTemplate with variables |
| `/role?role=...&question=...` | GET | Role-based prompting |
| `/few-shot?word=...` | GET | Few-shot learning with examples |
| `/chain-of-thought?problem=...` | GET | Chain of Thought (CoT) reasoning |

**Key Concepts:** Prompt Engineering, Templates, Few-shot Learning, Chain of Thought

---

### Part 4: Generation Parameters (`/generation-params/*`)
Control creativity and randomness in LLM outputs.

| Endpoint | Method | What It Teaches |
|----------|--------|-----------------|
| `/temperature-compare` | GET | Side-by-side temperature comparison (0.0 → 1.5) |
| `/custom?prompt=...&temperature=...&topP=...` | GET | Custom temperature and top-p values |
| `/deterministic?runs=...` | GET | Reproducible outputs with temp=0 |
| `/creative?runs=...` | GET | Random outputs with high temperature |

**Key Concepts:** Temperature, Top-K, Top-P, Probability Distribution, Token Selection

---

### Part 5: Embeddings (`/embeddings/*`)
Convert text to vectors and find semantic similarity.

| Endpoint | Method | What It Teaches |
|----------|--------|-----------------|
| `/generate?text=...` | GET | What embeddings look like (vector preview) |
| `/compare?text1=...&text2=...` | GET | Cosine similarity between two texts |
| `/find-similar?query=...&candidates=...` | GET | Semantic search - foundation for RAG |

**Key Concepts:** Embeddings, Vectors, Cosine Similarity, Semantic Search

---

### Part 6: Context Window (`/context-window/*`)
Understand LLM memory limits and strategies.

| Endpoint | Method | What It Teaches |
|----------|--------|-----------------|
| `/retention-test` | GET | How LLMs remember within a prompt |
| `/truncation-demo` | GET | Effect of max_tokens on responses |
| `/position-effect` | GET | "Lost in the middle" phenomenon |
| `/estimate-tokens?text=...` | GET | Token count estimation |

**Key Concepts:** Context Window, Truncation, Token Limits, Attention

---

### Part 7: Smart Study Assistant (`/study-assistant/*`)
Complete RAG application integrating all concepts.

| Endpoint | Method | What It Teaches |
|----------|--------|-----------------|
| `POST /notes` | POST | Store notes as embeddings |
| `POST /ask` | POST | Full RAG pipeline (retrieve → augment → generate) |
| `POST /compare-rag` | POST | Compare answers with vs without context |
| `GET /status` | GET | Check knowledge base size |
| `DELETE /notes` | DELETE | Clear all stored notes |

**Key Concepts:** RAG (Retrieval-Augmented Generation), Vector Store, Knowledge Base, Grounded Generation

---

### Part 8: 🎯 Student Task - Movie Recommendation Engine (`/movie-engine/*`)
**Hands-on challenge integrating all concepts from Parts 2-6!**

| Endpoint | Method | Task |
|----------|--------|------|
| `/find-similar?mood=...&topN=...` | GET | **TASK 1:** Use embeddings to find similar movies |
| `/recommend?mood=...` | GET | **TASK 2:** Generate recommendations with prompt templates |
| `/creative-recommend?mood=...&creativityLevel=...` | GET | **TASK 3:** Control creativity with temperature |
| `/chat?sessionId=...&message=...` | GET | **TASK 4:** Conversational mode with context management |
| `/full-experience?mood=...&creativityLevel=...` | GET | **TASK 5:** Capstone combining all concepts! |
| `/health` | GET | Check status (provided) |
| `/movies` | GET | List all movies in database (provided) |

**🎓 See [STUDENT_TASK.md](STUDENT_TASK.md) for full instructions, grading rubric, and hints!**

---

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- **Either** Ollama (free, local) **or** OpenAI API key (paid)

---

## 🆓 Option 1: Ollama Setup (FREE - Recommended)

Ollama runs LLMs locally on your machine. No API keys, no costs, no internet required!

### Step 1: Install Ollama

**macOS:**
```bash
brew install ollama
```

**Linux:**
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

**Windows:**
Download from [ollama.com/download](https://ollama.com/download)

### Step 2: Start Ollama Server

Open a terminal and run:
```bash
ollama serve
```
> ⚠️ **Keep this terminal open!** Ollama needs to be running for the app to work.

### Step 3: Download Required Models

Open a **new terminal** and download both models:

```bash
# Chat model for text generation (~2GB)
ollama pull llama3.2:3b

# Embedding model for vector search (~274MB)
ollama pull nomic-embed-text
```

### Step 4: Verify Models Are Ready

```bash
ollama list
```
You should see:
```
NAME                  SIZE
llama3.2:3b          2.0 GB
nomic-embed-text     274 MB
```

### Step 5: Run the Application

```bash
./gradlew bootRun
```

### Step 6: Test It!

```bash
curl http://localhost:8080/basic-prompt/hello
```

### 🎉 That's it! You're running AI locally for free!

---

## 💳 Option 2: OpenAI Setup (Paid)

If you prefer using OpenAI's models (faster, higher quality):

### Step 1: Get an API Key

1. Go to [platform.openai.com](https://platform.openai.com)
2. Create an account and add credits (~$5 minimum)
3. Generate an API key

### Step 2: Update Configuration

Edit `src/main/resources/application.properties`:

```properties
# Comment out Ollama lines:
# spring.ai.openai.api-key=${OPENAI_API_KEY:ollama}
# spring.ai.openai.base-url=${OPENAI_BASE_URL:http://localhost:11434}

# Uncomment OpenAI lines:
spring.ai.openai.api-key=sk-your-actual-key-here
spring.ai.openai.base-url=https://api.openai.com

# Update models:
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.embedding.options.model=text-embedding-3-small
```

### Step 3: Run the Application

```bash
./gradlew bootRun
```

---

## 🔧 Troubleshooting

### Ollama Issues

| Problem | Solution |
|---------|----------|
| "Connection refused" | Make sure `ollama serve` is running in another terminal |
| "Model not found" | Run `ollama pull llama3.2:3b` and `ollama pull nomic-embed-text` |
| Slow responses | Use a smaller model: `ollama pull llama3.2:1b` |
| Out of memory | Close other apps, or use `llama3.2:1b` instead |

### OpenAI Issues

| Problem | Solution |
|---------|----------|
| "Insufficient quota" | Add credits at platform.openai.com/settings/billing |
| "Model not found" | Check if your account has access to the model |
| 401 Unauthorized | Verify your API key is correct |

---



## 📖 Learning Path

### Part 1: Understanding the Configuration

Open `src/main/resources/application.properties` to see:
- How the LLM connection is configured
- What temperature, top-p, and other parameters mean
- How to switch between models

### Part 2: Basic Prompts 
**Endpoint:** `/basic-prompt`

**Concepts:** Prompts, Tokens, Context Window

```bash
# Your first LLM interaction
curl "http://localhost:8080/basic-prompt/hello"

# Custom question
curl "http://localhost:8080/basic-prompt/ask?prompt=What%20is%20machine%20learning?"

# With system prompt
curl "http://localhost:8080/basic-prompt/with-system?prompt=Explain%20vectors"

# Token behavior demo
curl "http://localhost:8080/basic-prompt/token-demo"
```

### Part 3: Prompt Templates
**Endpoint:** `/prompt-templates`

**Concepts:** Prompt Engineering, Templates, Few-shot Learning

```bash
# Bad vs Good prompt comparison
curl "http://localhost:8080/prompt-templates/compare?topic=recursion"

# Template with variables
curl "http://localhost:8080/prompt-templates/explain?topic=APIs&audience=child&style=playful"

# Role-based prompting
curl "http://localhost:8080/prompt-templates/role?role=pirate&question=What%20is%20the%20internet?"

# Few-shot learning
curl "http://localhost:8080/prompt-templates/few-shot?word=defenestration"

# Chain of thought
curl "http://localhost:8080/prompt-templates/chain-of-thought?problem=If%20I%20have%2015%20apples%20and%20give%20away%203,%20how%20many%20do%20I%20have?"
```

### Part 4: Generation Parameters
**Endpoint:** `/generation-params`

**Concepts:** Temperature, Top-K, Top-P, Probability Distribution

```bash
# Compare different temperatures
curl "http://localhost:8080/generation-params/temperature-compare"

# Custom parameters
curl "http://localhost:8080/generation-params/custom?prompt=Write%20a%20haiku&temperature=0.3"
curl "http://localhost:8080/generation-params/custom?prompt=Write%20a%20haiku&temperature=1.5"

# Deterministic output (run multiple times - same result)
curl "http://localhost:8080/generation-params/deterministic?runs=3"

# Creative output (run multiple times - different results)
curl "http://localhost:8080/generation-params/creative?runs=3"
```

### Part 5: Embeddings
**Endpoint:** `/embeddings`

**Concepts:** Vectors, Semantic Similarity, Cosine Similarity

```bash
# Generate an embedding
curl "http://localhost:8080/embeddings/generate?text=Hello%20world"

# Compare similarity
curl "http://localhost:8080/embeddings/compare?text1=I%20love%20pizza&text2=Pizza%20is%20my%20favorite%20food"
curl "http://localhost:8080/embeddings/compare?text1=I%20love%20pizza&text2=The%20weather%20is%20nice"

# Find most similar (foundation for RAG)
curl "http://localhost:8080/embeddings/find-similar?query=How%20do%20I%20cook%20pasta?&candidates=Boil%20water%20and%20add%20noodles&candidates=The%20weather%20is%20sunny&candidates=Italian%20cuisine%20recipes"
```

### Part 6: Context Window
**Endpoint:** `/context-window`

**Concepts:** Context Limits, Truncation, Token Estimation

```bash
# Context retention demo
curl "http://localhost:8080/context-window/retention-test"

# Truncation demo
curl "http://localhost:8080/context-window/truncation-demo"

# Position effect ("lost in the middle")
curl "http://localhost:8080/context-window/position-effect"

# Token estimation
curl "http://localhost:8080/context-window/estimate-tokens?text=Hello%20world%20how%20are%20you%20today"
```

### Part 7: Smart Study Assistant (RAG Integration)
**Endpoint:** `/study-assistant`

**Concepts:** RAG, Vector Search, Integrated AI Application

```bash
# 1. Add study notes
curl -X POST http://localhost:8080/study-assistant/notes \
  -H "Content-Type: application/json" \
  -d '{
    "notes": [
      "Photosynthesis is the process by which plants convert sunlight into glucose and oxygen.",
      "The mitochondria is the powerhouse of the cell, producing ATP through cellular respiration.",
      "DNA contains the genetic instructions for all living organisms.",
      "The water cycle includes evaporation, condensation, precipitation, and collection."
    ]
  }'

# 2. Ask a question
curl -X POST http://localhost:8080/study-assistant/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What produces energy in cells?",
    "topK": 2,
    "temperature": 0.7
  }'

# 3. Compare with and without RAG
curl -X POST http://localhost:8080/study-assistant/compare-rag \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is photosynthesis?",
    "customContext": "In our class, we learned it uses chlorophyll."
  }'

# 4. Check status
curl http://localhost:8080/study-assistant/status

# 5. Clear notes
curl -X DELETE http://localhost:8080/study-assistant/notes
```

---

## 🧠 Key Concepts Reference

### Tokens
- LLMs don't see text as words - they see **tokens** (word pieces)
- "Hello world" ≈ 2 tokens
- "Unbelievable" ≈ 3 tokens ("un", "believ", "able")
- Cost and limits are measured in tokens

### Temperature (0.0 - 2.0)
| Value | Effect | Use Case |
|-------|--------|----------|
| 0.0 | Deterministic | Code, facts |
| 0.7 | Balanced | Chatbots |
| 1.0+ | Creative | Brainstorming |

### Top-P (Nucleus Sampling)
- Controls the probability mass considered
- Lower = More focused
- Higher = More diverse

### Context Window
- Maximum tokens the LLM can "see"
- Includes BOTH input AND output
- Strategies: Chunking, Summarization, RAG

### Embeddings
- Numerical vector representations of text
- Similar meanings = Similar vectors
- Enable semantic search

### RAG (Retrieval-Augmented Generation)
1. **Retrieve**: Find relevant documents using embeddings
2. **Augment**: Add retrieved content to prompt
3. **Generate**: LLM answers using context

---

## 📁 Project Structure

```
src/main/java/com/example/ai/basics/
├── AiBasicsApplication.java          # Spring Boot main class
├── controller/
│   ├── Part2BasicPromptController.java      # Basic prompts
│   ├── Part3PromptTemplateController.java   # Prompt engineering
│   ├── Part4GenerationParamsController.java # Temperature, Top-K, Top-P
│   ├── Part5EmbeddingsController.java       # Embeddings & similarity
│   ├── Part6ContextWindowController.java    # Context limits
│   └── Part7StudyAssistantController.java   # Integrated RAG app
└── service/
    └── SimpleVectorStore.java               # In-memory vector storage
```

---

## 🔧 Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `spring.ai.openai.api-key` | Your API key | Required |
| `spring.ai.openai.base-url` | API endpoint | `https://api.openai.com` |
| `spring.ai.openai.chat.options.model` | Chat model | `gpt-4o-mini` |
| `spring.ai.openai.chat.options.temperature` | Default temp | `0.7` |
| `spring.ai.openai.chat.options.max-tokens` | Max response | `1024` |
| `spring.ai.openai.embedding.options.model` | Embedding model | `text-embedding-3-small` |

---

## 🎯 Exercises for Students

### Beginner
1. Try Part 2 endpoints - observe how prompts work
2. Compare temperatures in Part 4
3. Calculate similarity between texts in Part 5

### Intermediate
1. Create a new prompt template for your use case
2. Experiment with Top-P and Temperature combinations
3. Add custom notes to the Study Assistant

### Advanced
1. Implement a new endpoint with chain-of-thought reasoning
2. Add metadata to the vector store (tags, dates)
3. Implement conversation history (multi-turn chat)

---

## 📚 Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Reference](https://platform.openai.com/docs/api-reference)
- [OpenAI Tokenizer](https://platform.openai.com/tokenizer)
- ["Lost in the Middle" Paper](https://arxiv.org/abs/2307.03172)

---

## 🤝 Contributing

This is an educational project. Feel free to:
- Add more examples
- Improve explanations
- Fix issues

---

**Happy Learning! 🚀**
