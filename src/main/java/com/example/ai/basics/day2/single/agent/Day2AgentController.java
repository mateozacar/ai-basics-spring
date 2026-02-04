package com.example.ai.basics.day2.single.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/day2/agent")
public class Day2AgentController {

    private final ChatClient chatClient;

    public Day2AgentController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * DEMO: Simple Agent with Tools
     * 
     * The LLM will:
     * 1. Receive the prompt "What is the weather in London?"
     * 2. Realize it has a tool `currentWeather`
     * 3. Pause generation, request execution of `currentWeather("London")`
     * 4. Spring AI executes the Java method
     * 5. The result "15°C, Rainy" is fed back to the LLM
     * 6. The LLM generates the final natural language response.
     * 
     * Try:
     * http://localhost:8080/day2/agent/chat?message=What%20is%20the%20weather%20in%20London%3F
     */
    @GetMapping("/chat")
    public String chatWithAgent(@RequestParam(defaultValue = "What is the weather in London?") String message) {
        String response = chatClient.prompt()
                .user(message)
                .functions("currentWeather", "sumNumbers") // Enable specific tools by name
                .call()
                .content();

        return response != null ? response : "No response generated.";
    }

    /**
     * DEMO: Agent Reasoning
     * 
     * Sometimes we want to see the "Thinking" process.
     * Some models support reasoning traces, or we can prompt for it.
     * 
     * Try:
     * http://localhost:8080/day2/agent/reasoning?message=If%20I%20arrive%20in%20Tokyo%20at%202pm%20today,%20what%20is%20the%20weather%20likely%20to%20be%3F
     */
    @GetMapping("/reasoning")
    public String chatWithReasoning(
            @RequestParam(defaultValue = "If I arrive in Tokyo at 2pm today, what is the weather likely to be?") String message) {
        String response = chatClient.prompt()
                .system("You are a helpful assistant. Before answering, explain your plan of which tools you will use.")
                .user(message)
                .functions("currentWeather")
                .call()
                .content();

        return response != null ? response : "No response generated.";
    }
}
