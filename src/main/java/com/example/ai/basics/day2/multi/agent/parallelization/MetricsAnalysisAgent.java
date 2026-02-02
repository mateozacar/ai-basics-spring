package com.example.ai.basics.day2.multi.agent.parallelization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class MetricsAnalysisAgent {

    private static final Logger logger = LoggerFactory.getLogger(MetricsAnalysisAgent.class);
    private final ChatClient chatClient;

    public MetricsAnalysisAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String analyze(String incident) {
        logger.info("📈 Metrics Analysis Agent: Starting analysis for incident...");
        String response = chatClient.prompt()
                .user("""
                        You are a performance engineer.
                        Analyze system metrics for this incident:
                        %s
                        """.formatted(incident))
                .call()
                .content();
        logger.info("✅ Metrics Analysis Agent: Analysis complete.");
        return response;
    }
}
