# 🤖 Day 2: Agents & Orchestration

Welcome to Day 2! Today we move beyond simple prompts and start building **Autonomous Agents** that can use tools, make plans, and interact with the world.

## 📂 Project Structure

All the code for today is in: `src/main/java/com/example/ai/basics/day2`

-### 2. Available Tools
The agent is equipped with "Tools" (Java Functions) that it can trigger:

*   **Weather Tool**: Fetches real-time weather using Open-Meteo API.
*   **Math Tool**: Performs accurate calculations.
*   **Sales/Support Tools**: specialized tools for different agents.

Tools are defined as standard Spring `@Bean`s returning `Function<Request, Response>`:

```java
@Bean("currentWeather")
@Description("Get the current weather for a city")
public Function<WeatherRequest, String> currentWeather() {
    return request -> {
        // Call API...
        return "25°C, Sunny";
    };
}
```

The Agent Controller registers these tools:
```java
chatClient.prompt()
    .user("What is the weather?")
    .functions("currentWeather") // Enable the tool
    .call();
```
- **Part 1: Simple Agents** (`Day2AgentController.java`)
  - How to give Tools to an LLM
  - Using Spring AI Function Calling
  
- **Part 2: Orchestration** (`Day2OrchestrationController.java`)
  - **Chain Pattern**: Connecting agents in a sequence (Writer -> Editor)
  - **Router Pattern**: Classifying intent to choose the right path

## 🚀 How to Run the Examples

Start the Spring Boot application and try these endpoints:

### Part 1: Agents & Tools
- **Chat with Tools**: 
  `http://localhost:8080/day2/agent/chat?message=What is the weather in Madrid?`
  *(Watch the logs to see the actual method calls!)*

- **Reasoning Trace**:
  `http://localhost:8080/day2/agent/reasoning?message=Plan a dinner for me in Tokyo`

### Part 2: Orchestration Patterns
- **Chain (Writer -> Editor)**:
  `http://localhost:8080/day2/orchestration/chain?topic=AI Agents`
  *(See how the raw draft is refined by the second agent)*

- **Router (Intent Classification)**:
  `http://localhost:8080/day2/orchestration/router?input=My laptop won't turn on`
  *(Try inputs for Sales, Support, or General chat)*

## 🧠 Key Concepts

### 1. What is an Agent?
An **Agent** is an LLM that can **ACT**. 
- **LLM**: The brain (reasoning).
- **Tools**: The hands (calculator, search, APIs).
- **Loop**: The process of Thinking -> Acting -> Observing -> Repeating.

### 2. Orchestration (Workflows)
Instead of letting one big agent do everything, we often split tasks.
- **Chain**: Step 1 connects to Step 2. Reliable.
- **Router**: Decide which expert to call. Efficient.

---
**Happy Coding!** 
