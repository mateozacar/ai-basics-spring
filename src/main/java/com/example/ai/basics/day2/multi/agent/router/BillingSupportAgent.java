package com.example.ai.basics.day2.multi.agent.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class BillingSupportAgent {

    private static final Logger logger = LoggerFactory.getLogger(BillingSupportAgent.class);
    private final ChatClient chatClient;

    public BillingSupportAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String handle(String input) {
        logger.info("💰 Billing Support Agent: Handling request...");
        return chatClient.prompt()
                .system("You are a Billing Specialist. Answer questions about invoices, payments, and subscriptions with a professional and helpful tone.")
                .user(input)
                .call()
                .content();
    }
}
