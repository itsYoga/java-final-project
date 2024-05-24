import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui(){
        // setup our gui and add a title
        super("Weather App");

        // configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the size of our gui (in pixels)
        setSize(450, 650);

        // load our gui at the center of the screen
        setLocationRelativeTo(null);

        // make our layout manager null to manually position our components within the gui
        setLayout(null);

        // prevent any resize of our gui
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        // search field
        JTextField searchTextField = new JTextField();

        // set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        // change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // location text
        JLabel locationText = new JLabel("Location: ");
        locationText.setBounds(15, 65, 351, 30);
        locationText.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(locationText);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/image/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("src/image/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/image/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        JButton weeklyForecastButton = new JButton("一周天氣預報");
        weeklyForecastButton.setBounds(100, 100, 200, 50);
        weeklyForecastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject weekWeatherData = WeeklyForecast.getWeatherData((String) weatherData.get("county"), (String) weatherData.get("town"));
                JFrame forecastFrame = new JFrame("一周天氣預報");
                forecastFrame.setSize(1200, 500);
                forecastFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                forecastFrame.setLayout(new GridLayout(1, 7));
                for (int i = 0; i < 7; i++) {
                    JPanel panel = new JPanel();
                   panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    panel.setLayout(new BorderLayout());

                    JLabel weekdayLabel = new JLabel("<html>" + weekWeatherData.get("week"+i)+ "<br></html>", SwingConstants.CENTER);
                    weekdayLabel.setBounds(3, 65, 150, 34);
                    weekdayLabel.setFont(new Font("Dialog", Font.BOLD, 25));
                    panel.add(weekdayLabel);

                    String weatherConditions = (String) weekWeatherData.get("Wx"+i);
                    JLabel imageLabel = new JLabel();
                    imageLabel.setBounds(35, 115, 150, 150);
                    SetIcon.setIcon(imageLabel, weatherConditions);
                    SetIcon.modifyIcon(imageLabel,100,100);
                    panel.add(imageLabel);

                    JLabel temperatureLabel = new JLabel( "<html>"+weekWeatherData.get("MinT"+i)+ "C ~ " + weekWeatherData.get("MaxT"+i)+"C"+ "<br></html>", SwingConstants.CENTER);
                    temperatureLabel.setBounds(20,235,150,100);
                    temperatureLabel.setFont(new Font("Dialog", Font.PLAIN, 22));
                    panel.add(temperatureLabel, BorderLayout.SOUTH);

                    JLabel conditionsLabel = new JLabel( "<html>" + weatherConditions + "<br></html>", SwingConstants.CENTER);
                    conditionsLabel.setBounds(0,285,180,100);
                    conditionsLabel.setFont(new Font("Dialog", Font.BOLD, 22));
                    panel.add(conditionsLabel, BorderLayout.SOUTH);

                    JPanel panel1 = new JPanel();
                    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
                    JLabel imageRain = new JLabel();
                    imageRain.setIcon(loadImage("src/image/humidity.png"));
                    SetIcon.modifyIcon(imageRain,60,60);
                    imageRain.setBounds(0,375,60,60);
                    panel1.add(imageRain);

                    JLabel rainLabel = new JLabel("<html>降雨機率:<br>" +weekWeatherData.get("PoP12h"+i)+ "%</html>");
                    rainLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
                    panel1.add(rainLabel);

                    panel.add(panel1, BorderLayout.SOUTH);
                    forecastFrame.add(panel);
                }

                forecastFrame.setVisible(true);}

        });
        add(weeklyForecastButton);
        weeklyForecastButton.setVisible(false);
        // search button
        JButton searchButton = new JButton(loadImage("src/image/search.png"));

        // change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                if (userInput.length() >= 4 && (userInput.endsWith("大學") || userInput.endsWith("學院"))){
                    if (userInput.contains("台")) {
                        userInput = userInput.replace("台", "臺");
                    }
                    JSONObject collegeData = new JSONObject();
                    JSONParser parser = new JSONParser();
                    try (FileReader reader = new FileReader("college.json")) {
                        collegeData=(JSONObject) parser.parse(reader);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        collegeData= null;
                    }finally {
                        JSONObject collegeInfo = (JSONObject) collegeData.get(userInput);
                        if (collegeInfo != null) {
                            userInput = (String) collegeInfo.get("地區");
                        }else{
                            userInput = null;
                        }
                    }
                }
                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);
                if (weatherData == null) {
                    weeklyForecastButton.setVisible(false);
                    System.out.println("Error!");
                } else {
                    weeklyForecastButton.setVisible(true);
                    // update location text
                    locationText.setText("Location: " + userInput);

                    // update weather image
                    String weatherCondition = (String) weatherData.get("weather_condition");

                    SetIcon.setIcon(weatherConditionImage, weatherCondition);

                    // update temperature text
                    double temperature = (double) weatherData.get("temperature");
                    temperatureText.setText(temperature + " C");

                    // update weather condition text
                    weatherConditionDesc.setText(weatherCondition);

                    // update humidity text
                    double humidity = (double) weatherData.get("humidity");
                    humidityText.setText("<html><b>當日降水量</b> " + humidity + "mm</html>");

                    // update windspeed text
                    double windspeed = (double) weatherData.get("windspeed");
                    windspeedText.setText("<html><b>平均風風速</b> " + windspeed + "m/s</html>");
                }
            }
        });
        add(searchButton);

    }

    // used to create images in our gui components
    private ImageIcon loadImage(String resourcePath){
        try{
            // read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // returns an image icon so that our component can render it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
