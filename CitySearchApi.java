import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class CitySearchApi {
    private static final String RAPID_API_KEY = "355b1e8335mshc6b1af10a2c531fp159518jsn68a7ce10f7e7";
    private static final String RAPID_API_HOST = "wft-geo-db.p.rapidapi.com";
    private static final String BASE_URL = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?limit=10&minPopulation=50000&sort=-population&namePrefix=";


    public static List<String> getCitySuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlString = BASE_URL + encodedQuery;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", RAPID_API_KEY);
            conn.setRequestProperty("X-RapidAPI-Host", RAPID_API_HOST);

            if (conn.getResponseCode() != 200) return suggestions;

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray dataArray = jsonResponse.getJSONArray("data");
            System.out.println("API raw response: " + jsonResponse.toString(2));

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject cityObj = dataArray.getJSONObject(i);
                String city = cityObj.getString("city");
                String region = cityObj.has("region") ? ", " + cityObj.getString("region") : "";
                String country = cityObj.getString("country");
                suggestions.add(city + region + ", " + country);
                System.out.println("Suggestions: " + suggestions);
            }

        } catch (Exception e) {
            System.out.println("CitySearch API error: " + e.getMessage());
        }
        return suggestions;
    }
}
