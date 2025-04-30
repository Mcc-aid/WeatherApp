import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.json.JSONArray;

public class WeatherApi {
    private static final String API_KEY = "af85ae99171d409a94420641252602";
    // current weather endpoint
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json?key=";
    // forecast endpoint
    private static final String FORECAST_URL = "http://api.weatherapi.com/v1/forecast.json?key=";

    /**
     * Grabs weather data from a location given by the user
     */
    public static WeatherData getWeather(String location) {
        try {
            // 1) encode & call current.json
            String encodedLocation = URLEncoder.encode(location, "UTF-8");
            String urlString = BASE_URL + API_KEY + "&q=" + encodedLocation;
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                System.out.println("Error fetching weather: " +
                    conn.getResponseCode() + " " + conn.getResponseMessage());
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            // parse current.json
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject current   = jsonResponse.getJSONObject("current");
            JSONObject condition = current.getJSONObject("condition");

            // default sunrise/sunset (if forecast call fails)
            String sunrise = "N/A";
            String sunset  = "N/A";

            // 2) now call forecast.json?days=1 to get astro.sunrise & astro.sunset
            String fcUrl = FORECAST_URL + API_KEY 
                         + "&q=" + encodedLocation 
                         + "&days=1&aqi=no&alerts=no";
            HttpURLConnection fcConn = (HttpURLConnection) new URL(fcUrl).openConnection();
            fcConn.setRequestMethod("GET");
            if (fcConn.getResponseCode() == 200) {
                BufferedReader fin = new BufferedReader(new InputStreamReader(fcConn.getInputStream()));
                StringBuilder fResp = new StringBuilder();
                while ((line = fin.readLine()) != null) fResp.append(line);
                fin.close();

                JSONObject fJson = new JSONObject(fResp.toString());
                JSONArray  days  = fJson
                    .getJSONObject("forecast")
                    .getJSONArray("forecastday");
                if (days.length() > 0) {
                    JSONObject astro = days
                        .getJSONObject(0)
                        .getJSONObject("astro");
                    // these come in like "05:32 AM" / "07:45 PM"
                    sunrise = astro.optString("sunrise", "N/A");
                    sunset  = astro.optString("sunset",  "N/A");
                }
            }

            // 3) build your WeatherData with sunrise & sunset
            return new WeatherData(
                jsonResponse.getJSONObject("location").getString("localtime"),
                current.getInt("temp_c"),
                current.getInt("temp_f"),
                current.getInt("humidity"),
                current.getInt("wind_mph"),
                current.getInt("wind_kph"),
                current.getString("wind_dir"),
                current.getInt("feelslike_c"),
                current.getInt("feelslike_f"),
                current.getInt("uv"),
                condition.getString("text"),
                condition.getString("icon"),
                current.has("chance_of_rain") 
                    ? current.getInt("chance_of_rain") 
                    : 0,
                sunrise,  // <— real sunrise from forecast
                sunset    // <— real sunset  from forecast
            );

        } catch (Exception e) {
            System.out.println("Error fetching weather: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns a pipe‑delimited week forecast string exactly as before
     */
    public static String getWeekForecast(String location) {
        try {
            String encodedLocation = URLEncoder.encode(location, "UTF-8");
            String urlString = FORECAST_URL + API_KEY + "&q=" + encodedLocation + "&days=7";
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                return "Error fetching weather: " + conn.getResponseCode() + " " + conn.getResponseMessage();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder forecastResponse = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                forecastResponse.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(forecastResponse.toString());
            JSONArray forecastDays = jsonResponse
                .getJSONObject("forecast")
                .getJSONArray("forecastday");

            StringBuilder weeklyForecast = new StringBuilder();
            for (int i = 0; i < forecastDays.length(); i++) {
                JSONObject weekDay = forecastDays.getJSONObject(i);
                String date = weekDay.getString("date");
                String dayOfWeek = getWeekday(date);
                int maxTempF = weekDay.getJSONObject("day").getInt("maxtemp_f");
                int minTempF = weekDay.getJSONObject("day").getInt("mintemp_f");
                String condition = weekDay
                    .getJSONObject("day")
                    .getJSONObject("condition")
                    .getString("text");

                weeklyForecast
                  .append(dayOfWeek)
                  .append(" (").append(date).append("): ")
                  .append(minTempF).append("°F - ")
                  .append(maxTempF).append("°F, ")
                  .append(condition)
                  .append(" | ");
            }

            return weeklyForecast.toString();

        } catch (Exception e) {
            return "Error fetching week's forecast: " + e.getMessage();
        }
    }

    private static String getWeekday(String date) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            return localDate.getDayOfWeek().toString(); // e.g. "TUESDAY"
        } catch (Exception e) {
            return "UnknownDay";
        }
    }
}
