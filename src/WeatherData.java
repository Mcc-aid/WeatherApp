
public class WeatherData 
{
	private String lastUpdated;
	private int cTemps;
	private int fTemps;
	private int humidity;
	private int windSpeedMph;
	private String windDirection;
	private int feelsLikeC;
	private int feelsLikeF;
	private int uvIndex;
    private String conditionText;
    private String conditionIcon;
    private int chanceOfRain;
    private String sunrise;
    private String sunset;

    public WeatherData(String lastUpdated, int temperatureC, int temperatureF, int humidity,
            int windSpeedKph, int windSpeedMph, String windDirection,
            int feelsLikeC, int feelsLikeF, int uvIndex, String conditionText,
            String conditionIcon, int chanceOfRain, String sunrise, String sunset) 
    {
    		
    	this.lastUpdated = lastUpdated;
    	this.cTemps = temperatureC;
    	this.fTemps = temperatureF;
    	this.humidity = humidity;
    	this.windSpeedMph = windSpeedMph;
    	this.windDirection = windDirection;
    	this.feelsLikeC = feelsLikeC;
    	this.feelsLikeF = feelsLikeF;
    	this.uvIndex = uvIndex;
    	this.conditionText = conditionText;
    	this.conditionIcon = conditionIcon;
    	this.chanceOfRain = chanceOfRain;
    	this.sunrise = sunrise;
    	this.sunset = sunset;
    }
    // Getters
    public String getLastUpdated() 
    { 
    	return lastUpdated; 
    }
    public int getTemperatureC() 
    { 
    	return cTemps; 
    }
    public int getTemperatureF() 
    { 
    	return fTemps; 
    }
    public int getHumidity() 
    { 
    	return humidity;
    }
    public int getWindSpeedMph() 
    { 
    	return windSpeedMph; 
    }
    public String getWindDirection()
    { 
    	return windDirection; 
    }
    public int getFeelsLikeC()
    { 
    	return feelsLikeC; 
    }
    public int getFeelsLikeF() 
    {
    	return feelsLikeF; 
    }
    public int getUvIndex()
    {
    	return uvIndex; 
    }
    public String getConditionText() 
    {
    	return conditionText;
    }
    public String getConditionIcon() 
    { 
    	return conditionIcon;
    }
    public int getChanceOfRain() 
    { 
    	return chanceOfRain; 
    }
    public String getSunrise()
    {
    	return sunrise; 
    }
    public String getSunset() 
    { 
    	return sunset; 
    }
	
    @Override
    public String toString() {
        return "Last Updated: " + lastUpdated + "\n" +
               "Temperature: " + cTemps + "°C / " + fTemps + "°F\n" +
               "Feels Like: " + feelsLikeC + "°C / " + feelsLikeF + "°F\n" +
               "Humidity: " + humidity + "%\n" +
               "Wind: "  + windSpeedMph + " mph), " + windDirection + "\n" +
               "UV Index: " + uvIndex + "\n" +
               "Chance of Rain: " + chanceOfRain + "%\n" +
               "Sunrise: " + sunrise + "\n" +
               "Sunset: " + sunset + "\n" +
               "Condition: " + conditionText + "\n" +
               "Icon: " + conditionIcon;
    }
    // icon will be part of GUI in iteration 2  
}
