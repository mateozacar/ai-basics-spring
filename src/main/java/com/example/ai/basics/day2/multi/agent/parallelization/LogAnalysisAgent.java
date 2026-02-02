package com.example.ai.basics.day2.multi.agent.parallelization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class LogAnalysisAgent {

    private static final Logger logger = LoggerFactory.getLogger(LogAnalysisAgent.class);
    private final ChatClient chatClient;

    public LogAnalysisAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String analyze(String incident) {
        logger.info("📜 Log Analysis Agent: Starting analysis for incident...");
        String response = chatClient.prompt()
                .user("""
                        You are a log analysis expert.
                        Analyze logs related to this incident:
                        %s
                        """.formatted(incident))
                .call()
                .content();
        logger.info("✅ Log Analysis Agent: Analysis complete.");
        return response;
    }
}
