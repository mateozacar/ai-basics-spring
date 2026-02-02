package com.example.ai.basics.day2.multi.agent.workflow;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class PlannerAgent {

    private final ChatClient chatClient;

    public PlannerAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String createPlan(String incidentDescription) {
        String prompt = """
                You are a senior SRE.
                Analyze the following incident and produce a numbered investigation plan.

                Incident:
                %s

                Rules:
                - Max 3 steps
                - Each step must be concrete and actionable
                """.formatted(incidentDescription);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
