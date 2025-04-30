import java.util.Scanner;

public class Menu {

    public static void main(String[] args){
        Scanner scnr = new Scanner(System.in);
        boolean flag = true;

        while(flag){
            System.out.println("1: Select your location");
            System.out.println("2: Display the weather");                    
            System.out.println("3: Displays week's forecast");                    
            System.out.println("4: Change temperature unit");                
            System.out.println("5: Exit program");

            if (scnr.hasNextInt()) {
                int selection = scnr.nextInt();
                scnr.nextLine();  
                
                switch (selection) {
                    case 1:
                        System.out.print("Enter location: ");
                        String location = scnr.nextLine(); 

                        WeatherData weather = WeatherApi.getWeather(location);
                        if (weather != null) {
                            System.out.println(weather.toString()); 
                        } else {
                            System.out.println("Failed to fetch weather data.");
                        }
                        break;
                    //Add in iteration 2
                    case 2:
                        System.out.println("Weather is displayed here...");
                        break;
                    case 3: 
                        System.out.print("Enter location for weekly forecast: ");
                        String forecastLocation = scnr.nextLine(); 
                        
                        String weeklyForecast = WeatherApi.getWeekForecast(forecastLocation);
                        System.out.println(weeklyForecast); 
                        break;
                    // add in iteration 2
                    case 4:
                        System.out.println("Temperature unit changed.");
                        break;
                    case 5:
                        flag = false;
                        System.out.println("Thank you for using our app!");
                        break;
                    default:
                        System.out.println("Invalid selection. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scnr.next(); 
            }
        }
        scnr.close();
    }


	public static void selectLoc()
	{
		System.out.println("Welcome to loaction selector");
	}
	
	
}//end Menu
