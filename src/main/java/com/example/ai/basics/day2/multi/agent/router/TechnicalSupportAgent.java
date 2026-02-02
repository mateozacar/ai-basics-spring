package com.example.ai.basics.day2.multi.agent.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class TechnicalSupportAgent {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalSupportAgent.class);
    private final ChatClient chatClient;

    public TechnicalSupportAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String handle(String input) {
        logger.info("🔧 Technical Support Agent: Handling request...");
        return chatClient.prompt()
                .system("You are a Technical Support Expert. Provide detailed technical troubleshooting steps for the user's issue.")
                .user(input)
                .call()
                .content();
    }
}
