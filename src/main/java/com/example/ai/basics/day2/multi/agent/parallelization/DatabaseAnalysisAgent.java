package com.example.ai.basics.day2.multi.agent.parallelization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class DatabaseAnalysisAgent {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAnalysisAgent.class);
    private final ChatClient chatClient;

    public DatabaseAnalysisAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String analyze(String incident) {
        logger.info("🗄️ Database Analysis Agent: Starting analysis for incident...");
        String response = chatClient.prompt()
                .user("""
                        You are a database expert.
                        Analyze database-related causes for:
                        %s
                        """.formatted(incident))
                .call()
                .content();
        logger.info("✅ Database Analysis Agent: Analysis complete.");
        return response;
    }
}