package com.example.ai.basics.day2.single.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Configuration
public class AgentTools {

    private static final Logger logger = LoggerFactory.getLogger(AgentTools.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record WeatherRequest(String city) {
    }

    @Bean("currentWeather")
    @Description("Get the current weather for a city using the Open-Meteo API.")
    public Function<WeatherRequest, String> currentWeather() {
        return request -> {
            String city = request.city();
            logger.info("🌤️ Tool called: currentWeather for {}", city);
            try {
                // 1. Geocoding
                var httpClient = HttpClient.newHttpClient();
                var geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=en&format=json"
                        .formatted(URLEncoder.encode(city, StandardCharsets.UTF_8));

                var geoRequest = HttpRequest.newBuilder().uri(URI.create(geoUrl)).build();
                var geoResponse = httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode geoJson = objectMapper.readTree(geoResponse.body());

                if (!geoJson.has("results") || geoJson.get("results").isEmpty()) {
                    return "City not found: " + city;
                }

                JsonNode location = geoJson.get("results").get(0);
                double lat = location.get("latitude").asDouble();
                double lon = location.get("longitude").asDouble();
                String name = location.get("name").asText();
                String country = location.has("country") ? location.get("country").asText() : "Unknown";

                // 2. Weather
                var weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,weather_code&wind_speed_unit=ms"
                        .formatted(lat, lon);

                var weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).build();
                var weatherResponse = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode weatherJson = objectMapper.readTree(weatherResponse.body());

                JsonNode current = weatherJson.get("current");
                double temp = current.get("temperature_2m").asDouble();
                int humidity = current.get("relative_humidity_2m").asInt();
                int code = current.get("weather_code").asInt();

                String condition = getWeatherCondition(code);

                return String.format("Current weather in %s, %s: %.1f°C, %s, Humidity: %d%%",
                        name, country, temp, condition, humidity);

            } catch (Exception e) {
                logger.error("Error fetching weather", e);
                return "Error fetching weather data: " + e.getMessage();
            }
        };
    }

    private String getWeatherCondition(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Partly cloudy";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Unknown conditions (" + code + ")";
        };
    }

    public record SumRequest(int a, int b) {
    }

    @Bean("sumNumbers")
    @Description("Calculate the sum of two numbers.")
    public Function<SumRequest, Integer> sumNumbers() {
        return request -> request.a() + request.b();
    }
}
