package com.example.ai.basics.day2.multi.agent.workflow;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ReviewerAgent {

    private final ChatClient chatClient;

    public ReviewerAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String review(String originalIncident, String executionResult) {
        String prompt = """
                You are a technical reviewer.

                Incident:
                %s

                Findings:
                %s

                Tasks:
                - Check if the findings fully address the incident
                - Identify gaps
                - Suggest next actions
                """.formatted(originalIncident, executionResult);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
