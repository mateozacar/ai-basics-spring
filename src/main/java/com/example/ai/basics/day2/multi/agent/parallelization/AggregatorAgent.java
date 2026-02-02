package com.example.ai.basics.day2.multi.agent.parallelization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AggregatorAgent {

    private static final Logger logger = LoggerFactory.getLogger(AggregatorAgent.class);
    private final ChatClient chatClient;

    public AggregatorAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String aggregate(Map<String, String> findings) {
        logger.info("🕵️ Aggregator Agent: Starting discovery of root cause from all analysis...");
        String prompt = """
                You are an incident commander.
                Combine the following findings into a single root cause analysis:

                %s
                """.formatted(findings);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        logger.info("✅ Aggregator Agent: Final analysis complete.");
        return response;
    }
}
