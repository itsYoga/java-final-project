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

            if (locationName.length()>=5) {
                for (Object stationObj : stations) {
                    JSONObject station = (JSONObject) stationObj;
                    JSONObject geoInfo = (JSONObject) station.get("GeoInfo");
                    String countyName = geoInfo.get("CountyName").toString();
                    String townName = geoInfo.get("TownName").toString();

                    if (locationName.substring(0, 3).equals(countyName) && locationName.substring(3, locationName.length()).equals(townName)) {
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
                        return weatherData;
                    }
                }
            }else{
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
                    return weatherData;
                }
            }
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error: Could not connect to API");
        }

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




}







