package com.example.ai.basics.day2.multi.agent.workflow;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incident")
public class IncidentController {

    private final PlannerAgent planner;
    private final ExecutorAgent executor;
    private final ReviewerAgent reviewer;

    public IncidentController(
            PlannerAgent planner,
            ExecutorAgent executor,
            ReviewerAgent reviewer) {
        this.planner = planner;
        this.executor = executor;
        this.reviewer = reviewer;
    }

    /**
     * MULTI-AGENT INCIDENT INVESTIGATOR
     * 
     * Planner -> Executor -> Reviewer
     * 
     * Try: http://localhost:8080/incident?incident=My%20database%20is%20locked%20up
     */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IncidentController.class);

    @GetMapping
    public String investigate(
            @RequestParam(defaultValue = "My server is down and I see 500 errors in the logs") String incident) {

        logger.info("🚨 Incident Reported: {}", incident);
        String plan = planner.createPlan(incident);
        logger.info("📋 Plan Created:\n{}", plan);

        StringBuilder execution = new StringBuilder();
        for (String step : plan.split("\n")) {
            if (step.trim().isEmpty())
                continue; // Skip empty lines

            logger.info("⚙️ Executing Step: {}", step);
            execution.append("### ").append(step).append("\n");
            String result = executor.executeStep(step);
            execution.append(result).append("\n\n");
            logger.info("✅ Step Complete");
        }

        String review = reviewer.review(incident, execution.toString());

        return """
                ## Investigation Plan
                %s

                ## Execution
                %s

                ## Review
                %s
                """.formatted(plan, execution, review);
    }
}
