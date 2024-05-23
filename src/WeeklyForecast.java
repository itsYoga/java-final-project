import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
public class WeeklyForecast {
    private static String[] counties = {
            "宜蘭縣", "桃園市", "新竹縣", "苗栗縣", "彰化縣",
            "南投縣", "雲林縣", "嘉義縣", "屏東縣", "臺東縣",
            "花蓮縣", "澎湖縣", "基隆市", "新竹市", "嘉義市",
            "臺北市", "高雄市", "新北市", "臺中市", "臺南市",
            "連江縣", "金門縣", "台灣"
    };
    private static JSONObject weatherData = new JSONObject();
    public static JSONObject getWeatherData(String countryName,String townName ){
        int urlApiCount=-1;
        for (int i = 0; i < counties.length; i++) {
            if (countryName.equals(counties[i])) {
                urlApiCount = i * 4 + 3;
                break;
            }
        }
        String urlString = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-D0047-0"+urlApiCount+"?Authorization=CWA-7B91DB1C-EBA8-49D2-B113-4560F2A115ED&elementName=MinT,MaxT,PoP12h,Wx";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
                JSONObject records = (JSONObject) jsonResponse.get("records");
                JSONArray locations = (JSONArray) records.get("locations");

                for (Object locationObj : locations) {
                    JSONObject location = (JSONObject) locationObj;
                    if (countryName.equals(location.get("locationsName"))) {
                        JSONArray locationArray = (JSONArray) location.get("location");
                        for (Object locObj : locationArray) {
                            JSONObject loc = (JSONObject) locObj;
                            if (townName.equals(loc.get("locationName"))) {
                                JSONArray weatherElements = (JSONArray) loc.get("weatherElement");
                                for (Object weatherElementObj : weatherElements) {
                                    JSONObject weatherElement = (JSONObject) weatherElementObj;

                                    getApiData(weatherElement,"PoP12h");
                                    getApiData(weatherElement,"Wx");
                                    getApiData(weatherElement,"MinT");
                                    getApiData(weatherElement,"MaxT");

                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      return weatherData;
    }

    private static boolean isCurrentTimeInRange(String startTime, String endTime, LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);


        return (now.isEqual(start) || now.isAfter(start)) && now.isBefore(end);
    }

    private static String getCurrentDayOfWeek(LocalDateTime now) {
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.CHINESE);
    }

    private static void getApiData(JSONObject weatherElement, String elementName){
        if (elementName.equals(weatherElement.get("elementName"))) {
            JSONArray times = (JSONArray) weatherElement.get("time");
            LocalDateTime date = LocalDateTime.now();
            for (int i = 0; i < 7; i++){
                for (Object timeObj : times) {
                    JSONObject time = (JSONObject) timeObj;
                    String startTime = (String) time.get("startTime");
                    String endTime = (String) time.get("endTime");
                    JSONObject elementValue = (JSONObject) ((JSONArray) time.get("elementValue")).get(0);
                    String data = (String) elementValue.get("value");

                    if (isCurrentTimeInRange(startTime, endTime,date)) {
                        if(getCurrentDayOfWeek(LocalDateTime.now()).equals(getCurrentDayOfWeek(date)))
                            weatherData.put("week"+i, "今天");
                        else weatherData.put("week"+i, getCurrentDayOfWeek(date));
                        weatherData.put(elementName+i, data);



                    }
                }
                date=date.plusDays(1);
            }
        }
    }
}
