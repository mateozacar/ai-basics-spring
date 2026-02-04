package com.example.ai.basics.day2.multi.agent.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class RouterAgent {

    private static final Logger logger = LoggerFactory.getLogger(RouterAgent.class);
    private final ChatClient chatClient;

    public RouterAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String route(String input) {
        logger.info("🚦 Router Agent: Classifying input...");

        String category = chatClient.prompt()
                .system("""
                        Classify the user input into exactly one of these categories:
                        - TECHNICAL: Issues with software, bugs, installation, or performance.
                        - BILLING: Questions about invoices, payments, subscriptions, or refunds.
                        - GENERAL: Anything else.

                        Return ONLY the category name in uppercase.
                        """)
                .user(input)
                .call()
                .content()
                .trim()
                .toUpperCase();

        logger.info("🚦 Router Agent: Decided category -> {}", category);
        return category;
    }
}
