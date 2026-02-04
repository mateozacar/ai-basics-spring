package com.example.ai.basics.day2.multi.agent.parallelization;

import com.example.ai.basics.day2.multi.agent.workflow.ReviewerAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/incident/parallel")
public class ParallelIncidentController {

    private static final Logger logger = LoggerFactory.getLogger(ParallelIncidentController.class);
    private final ParallelIncidentService parallelService;
    private final AggregatorAgent aggregator;
    private final ReviewerAgent reviewer;

    public ParallelIncidentController(
            ParallelIncidentService parallelService,
            AggregatorAgent aggregator,
            ReviewerAgent reviewer) {
        this.parallelService = parallelService;
        this.aggregator = aggregator;
        this.reviewer = reviewer;
    }

    @GetMapping
    public String investigate(@RequestParam String incident) {
        logger.info("🎬 Parallel Investigation: Starting for incident '{}'", incident);

        Map<String, String> results = parallelService.runInParallel(incident);

        String aggregated = aggregator.aggregate(results);

        String review = reviewer.review(incident, aggregated);

        logger.info("🎬 Parallel Investigation: Fully complete.");

        return """
                ## Parallel Findings
                %s

                ## Aggregated Root Cause
                %s

                ## Review
                %s
                """.formatted(results, aggregated, review);
    }
}
