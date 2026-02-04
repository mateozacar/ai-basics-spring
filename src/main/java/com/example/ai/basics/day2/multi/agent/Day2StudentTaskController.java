package com.example.ai.basics.day2.multi.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * =============================================================================
 * 🎯 STUDENT TASK: BUILD A MULTI-AGENT TRAVEL CONCIERGE
 * =============================================================================
 * 
 * Welcome to Day 2! Today you will move beyond single prompts and build a
 * Multi-Agent System. You'll implement the patterns we learned:
 * 
 * ✅ Tool Use: Giving agents "hands" to interact with data
 * ✅ Router Pattern: Directing traffic to specialized agents
 * ✅ Parallelization: Running multiple agents at once for speed
 * ✅ Workflow Pattern: Sequential steps (Planner -> Executor -> Reviewer)
 * 
 * =============================================================================
 * ✈️ THE SCENARIO
 * =============================================================================
 * 
 * You are building "Globetrotter AI", a premium travel concierge.
 * Instead of one giant prompt, you will use specialized agents to handle
 * different parts of the travel planning process.
 * 
 * 1. A Router to classify the request.
 * 2. Specialized agents for Flights and Hotels (using tools).
 * 3. A Parallel researcher to find the best deals simultaneously.
 * 4. A Workflow to generate a polished, conflict-free itinerary.
 * 
 * =============================================================================
 * 📋 YOUR TASKS
 * =============================================================================
 * 
 * TASK 1: Implement searchTools() - FUNCTION CALLING
 * - Use the @Bean or function registry to give the LLM access to travel data
 * - Allow the agent to "search" for flights or hotels
 * 
 * TASK 2: Implement travelRouter() - ROUTER PATTERN
 * - Create a RouterAgent that classifies input: FLIGHT, HOTEL, or ITINERARY
 * - Route the request to the correct specialist
 * 
 * TASK 3: Implement parallelResearch() - PARALLELIZATION PATTERN
 * - Launch the Flight Agent and Hotel Agent at the same time
 * - Use CompletableFuture to run them in parallel
 * - Aggregate the results into a "Travel Package"
 * 
 * TASK 4: Implement itineraryWorkflow() - WORKFLOW PATTERN
 * - Implement a 3-step chain:
 * a) Planner: Creates a high-level schedule
 * b) Executor: Fills in the specific details using tools
 * c) Reviewer: Checks for overlaps or missing gaps
 * 
 * TASK 5: Implement getFullConciergeExperience() - CAPSTONE
 * - The ultimate Multi-Agent orchestration!
 * - Route -> Parallel Research -> Workflow Refinement
 * 
 * =============================================================================
 */
@RestController
@RequestMapping("/day2/task")
public class Day2StudentTaskController {

    private static final Logger logger = LoggerFactory.getLogger(Day2StudentTaskController.class);
    private final ChatClient chatClient;

    public Day2StudentTaskController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // =========================================================================
    // 🔍 TASK 1: THE SPECIALIST (Function Calling)
    // =========================================================================
    /**
     * Goal: Create an agent that can use tools to find information.
     * 
     * REQUIREMENTS:
     * 1. Register tools (functions) that the agent can call
     * 2. Use .functions("toolName") in the ChatClient call
     * 
     * TEST: GET http://localhost:8080/day2/task/search?query=Find me a flight to
     * Tokyo
     * 
     * HINT: Look at Day2AgentController.java for Tool usage.
     * The actual tool logic is already defined in TravelTools.java as @Beans.
     */
    @GetMapping("/search")
    public String searchWithTools(@RequestParam String query) {
        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // 1. Create a prompt that asks the LLM to find flight or hotel info
        // 2. Enable the tools "flightSearch" and "hotelSearch"
        // 3. Return the response
        // =====================================================================

        return """
                ⚠️ TASK 1 NOT IMPLEMENTED

                You need to implement searchWithTools()!
                - Query: "%s"
                - Requirement: Enable function calling for flight/hotel search.
                """.formatted(query);
    }

    // =========================================================================
    // 🚦 TASK 2: THE ROUTER (Router Pattern)
    // =========================================================================
    /**
     * Goal: Classify the user intent and route to a specialist.
     * 
     * REQUIREMENTS:
     * 1. First prompt: Classify input as FLIGHT, HOTEL, or OTHER
     * 2. Switch statement: Send the input to a specialized "sub-prompt"
     * 
     * TEST: GET http://localhost:8080/day2/task/route?input=I need a room in Paris
     * 
     * HINT: Look at RouterAgent.java and RoutingController.java
     */
    @GetMapping("/route")
    public String travelRouter(@RequestParam String input) {
        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // 1. Classify the input using a "Router Agent" (system prompt)
        // 2. Based on category, call a specific specialist logic
        // 3. Return the specialized response
        // =====================================================================

        return """
                ⚠️ TASK 2 NOT IMPLEMENTED

                You need to implement travelRouter()!
                - Input: "%s"
                - Requirement: Classify into FLIGHT/HOTEL/OTHER and route accordingly.
                """.formatted(input);
    }

    // =========================================================================
    // ⚡ TASK 3: THE RESEARCHER (Parallelization Pattern)
    // =========================================================================
    /**
     * Goal: Gather multiple pieces of information at once.
     * 
     * REQUIREMENTS:
     * 1. Start a Flight Search and a Hotel Search at the same time
     * 2. Use Java's CompletableFuture (or similar) to run in parallel
     * 3. An "Aggregator" agent combines both into a summary
     * 
     * TEST: GET http://localhost:8080/day2/task/parallel?destination=New York
     * 
     * HINT: Look at ParallelIncidentController.java
     */
    @GetMapping("/parallel")
    public String parallelResearch(@RequestParam String destination) {
        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // 1. Create two async tasks: fetchFlights(destination) and
        // fetchHotels(destination)
        // 2. Wait for both to complete
        // 3. Use an Aggregator prompt to summarize the combined results
        // =====================================================================

        return """
                ⚠️ TASK 3 NOT IMPLEMENTED

                You need to implement parallelResearch()!
                - Destination: "%s"
                - Requirement: Run flight and hotel searches in parallel.
                """.formatted(destination);
    }

    // =========================================================================
    // ⛓️ TASK 4: THE ARCHITECT (Workflow Pattern)
    // =========================================================================
    /**
     * Goal: Multi-step sequential processing.
     * 
     * REQUIREMENTS:
     * 1. Step 1 (Planner): Generate a basic 3-day itinerary
     * 2. Step 2 (Executor): Add specific flight/hotel options to the itinerary
     * 3. Step 3 (Reviewer): Review for logical errors (e.g. hotel check-in before
     * flight arrival)
     * 
     * TEST: GET http://localhost:8080/day2/task/workflow?destination=London
     * 
     * HINT: Look at Workflow package (PlannerAgent, ExecutorAgent, ReviewerAgent)
     */
    @GetMapping("/workflow")
    public String itineraryWorkflow(@RequestParam String destination) {
        // =====================================================================
        // TODO: IMPLEMENT THIS METHOD
        // 1. Call Planner -> get plan
        // 2. Pass plan to Executor -> get detailed plan
        // 3. Pass detailed plan to Reviewer -> get final itinerary
        // =====================================================================

        return """
                ⚠️ TASK 4 NOT IMPLEMENTED

                You need to implement itineraryWorkflow()!
                - Destination: "%s"
                - Requirement: Planner -> Executor -> Reviewer chain.
                """.formatted(destination);
    }

    // =========================================================================
    // 🌟 TASK 5: FULL CONCIERGE EXPERIENCE (Capstone)
    // =========================================================================
    /**
     * Goal: Combine EVERYTHING into one masterpiece.
     * 
     * REQUIREMENTS:
     * 1. Route the user request
     * 2. If it's a full trip, run parallel search
     * 3. Feed those results into the Itinerary workflow
     * 4. Use proper System Prompts and Creativity Control (Temperature)
     * 
     * TEST: GET http://localhost:8080/day2/task/capstone?prompt=Plan a 2 day luxury
     * trip to Rome
     */
    @GetMapping("/capstone")
    public String getFullConciergeExperience(@RequestParam String prompt) {
        // =====================================================================
        // TODO: THIS IS YOUR TIME TO SHINE!
        // Combine all the patterns above into a single flow.
        // =====================================================================

        return """
                ⚠️ TASK 5 NOT IMPLEMENTED

                You need to implement the Capstone Experience!
                - Prompt: "%s"
                - Use all patterns: Router, Parallel, Workflow, and Tools.
                """.formatted(prompt);
    }

    // =========================================================================
    // 📋 TRAVEL DATABASE & TOOLS (DO NOT MODIFY - Use these for Task 1)
    // =========================================================================

    // In a real app, these would be separate @Components or @Beans
    // For this task, we'll simulate them as inner record tools.

    public record Flight(String from, String to, String price, String duration) {
    }

    public record Hotel(String name, String city, String pricePerNight, String rating) {
    }

    /**
     * Tool for Flight Search (Simulated)
     * You should enable this for Task 1
     */
    @GetMapping("/tools/flights")
    public List<Flight> flightSearch(@RequestParam String destination) {
        logger.info("✈️ Searching for flights to {}", destination);
        return List.of(
                new Flight("Anywhere", destination, "$450", "8h 30m"),
                new Flight("Anywhere", destination, "$890", "7h 15m"));
    }

    /**
     * Tool for Hotel Search (Simulated)
     * You should enable this for Task 1
     */
    @GetMapping("/tools/hotels")
    public List<Hotel> hotelSearch(@RequestParam String city) {
        logger.info("🏨 Searching for hotels in {}", city);
        return List.of(
                new Hotel("Grand Plaza", city, "$200", "4.5/5"),
                new Hotel("Budget Inn", city, "$85", "3.2/5"));
    }

    @GetMapping("/health")
    public String check() {
        return "✅ Day 2 Task Controller is ready for students!";
    }
}
