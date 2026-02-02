package com.example.ai.basics.day2.multi.agent.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/multi-agent/router")
public class RoutingController {

    private static final Logger logger = LoggerFactory.getLogger(RoutingController.class);

    private final RouterAgent router;
    private final TechnicalSupportAgent technicalAgent;
    private final BillingSupportAgent billingAgent;
    private final GeneralSupportAgent generalAgent;

    public RoutingController(
            RouterAgent router,
            TechnicalSupportAgent technicalAgent,
            BillingSupportAgent billingAgent,
            GeneralSupportAgent generalAgent) {
        this.router = router;
        this.technicalAgent = technicalAgent;
        this.billingAgent = billingAgent;
        this.generalAgent = generalAgent;
    }

    /**
     * ROUTING WORKFLOW
     * 
     * Try:
     * 1. Technical:
     * http://localhost:8080/multi-agent/router?input=My%20app%20crashes%20on%20startup
     * 2. Billing:
     * http://localhost:8080/multi-agent/router?input=Where%20can%20I%20find%20my%20last%20invoice?
     * 3. General:
     * http://localhost:8080/multi-agent/router?input=I%20love%20your%20product
     */
    @GetMapping
    public String handleRequest(@RequestParam String input) {
        logger.info("🎬 Starting Routing Workflow for: {}", input);

        String category = router.route(input);

        String response = switch (category) {
            case "TECHNICAL" -> technicalAgent.handle(input);
            case "BILLING" -> billingAgent.handle(input);
            default -> generalAgent.handle(input);
        };

        logger.info("✅ Routing Workflow complete.");
        return """
                ## Routing Result
                **Category:** %s

                ---

                ## Response from Specialist
                %s
                """.formatted(category, response);
    }
}
