package com.example.ai.basics.day2.multi.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;

@Configuration
public class TravelTools {

    private static final Logger logger = LoggerFactory.getLogger(TravelTools.class);

    public record FlightRequest(String destination) {
    }

    public record HotelRequest(String city) {
    }

    @Bean("flightSearch")
    @Description("Search for available flights to a specific destination. Returns flight options with price and duration.")
    public Function<FlightRequest, String> flightSearch() {
        return request -> {
            logger.info("✈️ Tool: Searching flights to {}", request.destination());
            // Simulated data
            return """
                    Flights to %s:
                    1. SkyHigh Airways: $450 (Non-stop, 8h 30m)
                    2. OceanBlue Air: $890 (1-stop, 12h 15m)
                    3. Global Jet: $1,200 (Business Class, 7h 45m)
                    """.formatted(request.destination());
        };
    }

    @Bean("hotelSearch")
    @Description("Search for available hotels in a specific city. Returns hotel names, ratings, and price per night.")
    public Function<HotelRequest, String> hotelSearch() {
        return request -> {
            logger.info("🏨 Tool: Searching hotels in {}", request.city());
            // Simulated data
            return """
                    Hotels in %s:
                    1. Grand Plaza: $200/night (4.5 stars)
                    2. Riverside Boutique: $350/night (5.0 stars)
                    3. Cozy Corner Inn: $95/night (3.0 stars)
                    """.formatted(request.city());
        };
    }
}
