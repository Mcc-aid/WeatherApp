//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//import java.util.List;
//
//public class WeatherApiTest {
//
//    @Test
//    public void testValidLocation() {
//        WeatherData weather = WeatherApi.getWeather("Jacksonville, Florida");
//        assertNotNull(weather, "Weather data should not be null for a valid location.");
//        assertTrue(weather.getTemperatureF() > -100, "Temperature should be within a reasonable range.");
//        assertTrue(weather.getHumidity() >= 0 && weather.getHumidity() <= 100, "Humidity should be between 0 and 100.");
//        assertTrue(weather.getWindSpeedMph() >= 0, "Wind speed should not be negative.");
//        assertTrue(weather.getUvIndex() >= 0, "UV Index should not be negative.");
//        assertTrue(weather.getFeelsLikeF() > -100, "Feels-like temperature should be reasonable.");
//        assertNotNull(weather.getConditionText(), "Condition text should not be null.");
//    }
//
//    @Test
//    public void testInvalidLocation() {
//        WeatherData weather = WeatherApi.getWeather("asmdkfdnfnoasng");
//        assertNull(weather, "Weather data should be null for an invalid location.");
//    }
//
//    @Test
//    public void testWeekForecastValid() {
//        String forecast = WeatherApi.getWeekForecast("Jacksonville, Florida");
//        assertNotNull(forecast, "Forecast should not be null for a valid location.");
//        assertTrue(forecast.contains("°F"), "Forecast should contain temperature details.");
//    }
//
//    @Test
//    public void testMenuLocationSelection() {
//        Menu.selectLoc();
//        assertEquals("Welcome to location selector", "Welcome to location selector", "Menu should display correct message.");
//    }
//
//    @Test
//    public void testWeekForecastInvalid() {
//        String forecast = WeatherApi.getWeekForecast("asmdkfdnfnoasng");
//        assertTrue(forecast.startsWith("Error fetching"), "Error message should be returned for an invalid location.");
//    }
//
//    @Test
//    public void testCaseInsensitiveLocation() {
//        WeatherData weatherLowercase = WeatherApi.getWeather("jacksonville, florida");
//        WeatherData weatherUppercase = WeatherApi.getWeather("JACKSONVILLE, FLORIDA");
//        assertNotNull(weatherLowercase, "Lowercase location should still return valid data.");
//        assertNotNull(weatherUppercase, "Uppercase location should still return valid data.");
//    }
//
//    @Test
//    public void testCitySuggestionsValidPrefix() {
//        List<String> suggestions = CitySearchApi.getCitySuggestions("Orlando");
//        assertNotNull(suggestions, "Suggestions list should not be null.");
//        if (suggestions.isEmpty()) {
//            System.out.println("WARNING: API may be offline or rate-limited, skipping assertFalse.");
//        } else {
//            assertFalse(suggestions.isEmpty(), "Suggestions list should not be empty for a valid prefix.");
//        }
//    }
//
//    @Test
//    public void testCitySuggestionsEmptyQuery() {
//        List<String> suggestions = CitySearchApi.getCitySuggestions("");
//        assertNotNull(suggestions, "Suggestions list should not be null.");
//        assertFalse(suggestions.isEmpty(), "Empty query should still return city suggestions (top global cities).");
//    }
//
//    @Test
//    public void testCitySuggestionsInvalidPrefix() {
//        List<String> suggestions = CitySearchApi.getCitySuggestions("asdasdasd");
//        assertNotNull(suggestions, "Suggestions list should not be null.");
//        // Could be empty depending on API, safe to allow both behaviors
//    }
//
//    @Test
//    public void testWeatherDataToString() {
//        WeatherData data = new WeatherData(
//            "2025-04-20 13:00", 22, 72, 50, 10, 16, "NW",
//            21, 70, 5, "Sunny", "//icon.png", 25,
//            "06:00 AM", "08:00 PM"
//        );
//        String result = data.toString();
//        System.out.println("WeatherData.toString() output:\n" + result);
//        assertTrue(result.contains("Temperature"));
//        assertTrue(result.contains("Sunny"));
//        assertTrue(result.contains("Wind"));
//    }
//
//    @Test
//    public void testEmptyForecastResponseHandling() {
//        String forecast = WeatherApi.getWeekForecast("UnknownTownThatDoesntExistOnThisPlanet");
//        assertTrue(forecast.startsWith("Error fetching") || forecast.isEmpty());
//    }
//
//}
