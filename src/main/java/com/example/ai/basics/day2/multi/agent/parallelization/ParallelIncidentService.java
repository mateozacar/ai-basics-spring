package com.example.ai.basics.day2.multi.agent.parallelization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ParallelIncidentService {

        private static final Logger logger = LoggerFactory.getLogger(ParallelIncidentService.class);
        private final LogAnalysisAgent logAgent;
        private final MetricsAnalysisAgent metricsAgent;
        private final DatabaseAnalysisAgent dbAgent;

        public ParallelIncidentService(
                        LogAnalysisAgent logAgent,
                        MetricsAnalysisAgent metricsAgent,
                        DatabaseAnalysisAgent dbAgent) {
                this.logAgent = logAgent;
                this.metricsAgent = metricsAgent;
                this.dbAgent = dbAgent;
        }

        public Map<String, String> runInParallel(String incident) {
                logger.info("⚡ Parallel Incident Service: Starting parallel analysis...");

                CompletableFuture<String> logs = CompletableFuture.supplyAsync(() -> logAgent.analyze(incident));

                CompletableFuture<String> metrics = CompletableFuture.supplyAsync(() -> metricsAgent.analyze(incident));

                CompletableFuture<String> database = CompletableFuture.supplyAsync(() -> dbAgent.analyze(incident));

                CompletableFuture.allOf(logs, metrics, database).join();

                logger.info("⚡ Parallel Incident Service: All parallel tasks complete.");

                return Map.of(
                                "Logs", logs.join(),
                                "Metrics", metrics.join(),
                                "Database", database.join());
        }
}
