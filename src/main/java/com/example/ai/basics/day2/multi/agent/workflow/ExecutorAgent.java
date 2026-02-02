package com.example.ai.basics.day2.multi.agent.workflow;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ExecutorAgent {

    private final ChatClient chatClient;

    public ExecutorAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String executeStep(String step) {
        String prompt = """
                You are a software engineer.
                Execute the following investigation step and explain findings:

                Step:
                %s
                """.formatted(step);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
