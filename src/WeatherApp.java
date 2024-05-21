import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// retreive weather data from API - this backend logic will fetch the latest weather
// data from the external API and return it. The GUI will
// display this data to the user
public class WeatherApp {
    // fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){
        if (locationName.contains("台")) {
            locationName = locationName.replace("台", "臺");
        }

        // build API request URL with location coordinates
        String urlString = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/O-A0003-001?Authorization=CWA-7B91DB1C-EBA8-49D2-B113-4560F2A115ED&WeatherElement=Weather,Now,WindSpeed,AirTemperature&GeoInfo=CountyName,TownName";

        try{
            // call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check for response status
            // 200 - means that the connection was a success
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            // Read the response line by line and append to the response StringBuilder
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // Close the BufferedReader
            in.close();

            // close url connection
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
            // Get the 'records' object from the response
            JSONObject records = (JSONObject) jsonResponse.get("records");
            // Get the 'Station' array from the 'records' object
            JSONArray stations = (JSONArray) records.get("Station");

            if (locationName.length()>=6) {
                for (Object stationObj : stations) {
                    JSONObject station = (JSONObject) stationObj;
                    JSONObject geoInfo = (JSONObject) station.get("GeoInfo");
                    String countyName = geoInfo.get("CountyName").toString();
                    String townName = geoInfo.get("TownName").toString();

                    if (locationName.substring(0, 3).equals(countyName) && locationName.substring(locationName.length() - 3).equals(townName)) {
                        //String stationName = station.get("locationName").toString();
                        JSONObject weather = (JSONObject) station.get("WeatherElement");
                        String weatherCondition = weather.get("Weather").toString();
                        JSONObject nowHumidity = (JSONObject) weather.get("Now");
                        double humidity = (double) nowHumidity.get("Precipitation");
                        double windSpeed = (double) weather.get("WindSpeed");
                        double airTemperature = (double) weather.get("AirTemperature");
                        // build the weather json data object that we are going to access in our frontend
                        JSONObject weatherData = new JSONObject();
                        weatherData.put("county", countyName);
                        weatherData.put("town", townName);
                        weatherData.put("temperature", airTemperature);
                        weatherData.put("weather_condition", weatherCondition);
                        weatherData.put("humidity", humidity);
                        weatherData.put("windspeed", windSpeed);
                        //System.out.println("OKKKKK");
                        return weatherData;
                    }
                }
            }
            for (Object stationObj : stations){
                JSONObject station = (JSONObject) stationObj;
                String stationName = station.get("StationName").toString();
                if(locationName.substring(0, 2).equals(stationName)) {
                    JSONObject geoInfo = (JSONObject) station.get("GeoInfo");
                    String countyName = geoInfo.get("CountyName").toString();
                    String townName = geoInfo.get("TownName").toString();
                    JSONObject weather = (JSONObject) station.get("WeatherElement");
                    String weatherCondition = weather.get("Weather").toString();
                    JSONObject nowHumidity = (JSONObject) weather.get("Now");
                    double humidity = (double) nowHumidity.get("Precipitation");
                    double windSpeed = (double) weather.get("WindSpeed");
                    double airTemperature = (double) weather.get("AirTemperature");
                    // build the weather json data object that we are going to access in our frontend
                    JSONObject weatherData = new JSONObject();
                    weatherData.put("county", countyName);
                    weatherData.put("town", townName);
                    weatherData.put("temperature", airTemperature);
                    weatherData.put("weather_condition", weatherCondition);
                    weatherData.put("humidity", humidity);
                    weatherData.put("windspeed", windSpeed);
                    //System.out.println("OKKKKK");
                    return weatherData;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error: Could not connect to API");
        }

        return null;
    }

    // retrieves geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName){
        // replace any whitespace in location name to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        // build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            // call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check response status
            // 200 means successful connection
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                // store the API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // read and store the resulting json data into our string builder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                // close scanner
                scanner.close();

                // close url connection
                conn.disconnect();

                // parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the list of location data the API gtenerated from the lcoation name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        // couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            // attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            // connect to our API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        // could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // iterate through the time list and see which one matches our current time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // return the index
                return i;
            }
        }

        return 0;
    }

    private static String getCurrentTime(){
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be 2023-09-02T00:00 (this is how is is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }



}







