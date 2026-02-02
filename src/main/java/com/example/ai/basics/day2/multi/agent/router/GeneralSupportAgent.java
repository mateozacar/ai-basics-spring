package com.example.ai.basics.day2.multi.agent.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class GeneralSupportAgent {

    private static final Logger logger = LoggerFactory.getLogger(GeneralSupportAgent.class);
    private final ChatClient chatClient;

    public GeneralSupportAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String handle(String input) {
        logger.info("ℹ️ General Support Agent: Handling request...");
        return chatClient.prompt()
                .system("You are a Helpful Customer Service Representative. Handle general inquiries, appreciation, or miscellaneous questions.")
                .user(input)
                .call()
                .content();
    }
}
