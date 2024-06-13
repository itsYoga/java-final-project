import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    private String cctvurl;
    private  int subCurrentIndex = 0;
    TrayIcon i;
    public WeatherAppGui(){

        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450, 650);

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        addGuiComponents();

        new TrayIconManager(this);
    }

    private void addGuiComponents(){
        JTextField searchTextField = new JTextField();

        searchTextField.setBounds(15, 15, 351, 45);

        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        InfoReadWriter.start(subCurrentIndex, searchTextField);

        searchTextField.setText("海洋大學");

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    subCurrentIndex--;
                    if(!InfoReadWriter.showPreviousLine(subCurrentIndex, searchTextField)) {
                        subCurrentIndex++;
                    };
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    subCurrentIndex++;
                    if(!InfoReadWriter.showNextLine(subCurrentIndex, searchTextField)){
                        subCurrentIndex--;
                    }
                }
            }
        });

        // location text
        JLabel locationText = new JLabel("Location: ");
        locationText.setBounds(15, 65, 351, 30);
        locationText.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(locationText);

        JLabel weatherConditionImage = new JLabel(loadImage("src/image/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel("weather app");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        JLabel weatherConditionDesc = new JLabel("請搜尋地區");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        JLabel humidityImage = new JLabel(loadImage("src/image/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>降雨量</b> mm</html>");
        humidityText.setBounds(90, 500, 95, 65);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        JLabel windspeedImage = new JLabel(loadImage("src/image/windspeed.png"));
        windspeedImage.setBounds(220, 500, 105, 66);
        add(windspeedImage);

        JLabel windspeedText = new JLabel("<html><b>風速</b> m/s</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        JButton weeklyForecastButton = new JButton("一周天氣預報");
        weeklyForecastButton.setBounds(20, 100, 180, 50);
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

                    String pop=(String) weekWeatherData.get("PoP12h"+i);
                    if (pop.equals(" ")) pop="未觀測";
                    else pop=pop+"%";
                    JLabel rainLabel = new JLabel("<html>降雨機率:<br>" +pop+ "</html>");
                    rainLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
                    panel1.add(rainLabel);

                    panel.add(panel1, BorderLayout.SOUTH);
                    forecastFrame.add(panel);
                }

                forecastFrame.setVisible(true);}

        });
        add(weeklyForecastButton);
        weeklyForecastButton.setVisible(false);


        JButton viewLiveImageButton = new JButton("觀看即時影像");
        viewLiveImageButton.setBounds(250, 100, 180, 50); // Adjust position and size as needed
        viewLiveImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewLiveImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MjpegStreamPlayer.play(cctvurl);
                System.out.println("Viewing live image...");
            }
        });
        add(viewLiveImageButton);
        viewLiveImageButton.setVisible(false);
        JButton unsubscribeButton = new JButton("取消訂閱");
        unsubscribeButton.setBounds(325, 400, 100, 50); // Adjust position and size as needed
        unsubscribeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        unsubscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InfoReadWriter.removeInfo(searchTextField.getText());
            }
        });
        JButton subscribeButton = new JButton("訂閱");
        subscribeButton.setBounds(10, 400, 100, 50); // Adjust position and size as needed
        subscribeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        subscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InfoReadWriter.addInfo(searchTextField.getText());
                unsubscribeButton.setVisible(true);
            }
        });
        add(subscribeButton);
        subscribeButton.setVisible(false);


        add(unsubscribeButton);
        unsubscribeButton.setVisible(false);
        // search button
        JButton searchButton = new JButton(loadImage("src/image/search.png"));

        // change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        ActionListener searchActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    JOptionPane.showMessageDialog(null, "搜尋資料不得為空", "Error", JOptionPane.ERROR_MESSAGE);
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
                            if(collegeInfo.get("cctv")!=null){
                                viewLiveImageButton.setVisible(true);
                                cctvurl=(String) collegeInfo.get("cctv");
                            }else{
                                viewLiveImageButton.setVisible(false);
                                cctvurl=null;
                            }
                        }else{
                            userInput = null;
                        }
                    }
                }
                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                if (weatherData == null) {
                    weeklyForecastButton.setVisible(false);
                    subscribeButton.setVisible(false);
                    unsubscribeButton.setVisible(false);
                    viewLiveImageButton.setVisible(false);
                    JOptionPane.showMessageDialog(null, "搜尋資料有誤，請重新輸入", "Error", JOptionPane.ERROR_MESSAGE);

                } else {
                    if(InfoReadWriter.checkInfo(searchTextField.getText())) unsubscribeButton.setVisible(true);
                    else unsubscribeButton.setVisible(false);
                    weeklyForecastButton.setVisible(true);
                    subscribeButton.setVisible(true);
                    locationText.setText("Location: " + userInput);

                    // update weather image
                    String weatherCondition = (String) weatherData.get("weather_condition");

                    SetIcon.setIcon(weatherConditionImage, weatherCondition);

                    // update temperature text
                    double temperature = (double) weatherData.get("temperature");
                    if (temperature < -90)
                        temperatureText.setText("測站異常");
                    else
                        temperatureText.setText(temperature + " C");

                    // update weather condition text
                    weatherConditionDesc.setText(weatherCondition);

                    // update humidity text
                    double humidity = (double) weatherData.get("humidity");
                    if (humidity < -90)
                        humidityText.setText("<html><b>當日降水量</b> 數值異常</html>");
                    else
                        humidityText.setText("<html><b>當日降水量</b> " + humidity + "mm</html>");


                    // update windspeed text
                    double windspeed = (double) weatherData.get("windspeed");
                    if (windspeed < -90)
                        windspeedText.setText("<html><b>平均風速</b>  數值異常</html>");
                    else
                        windspeedText.setText("<html><b>平均風速</b> " + windspeed + "m/s</html>");
                }
            }
        };
        searchButton.addActionListener(searchActionListener);
        searchTextField.addActionListener(searchActionListener);
        add(searchButton);
        ImageIcon icon = new ImageIcon(loadImage("src/image/sound.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        JButton soundButton = new JButton(icon);

        // change the cursor to a hand cursor when hovering over this button
        soundButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        soundButton.setBounds(375, 63, 47, 45);

        soundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (weatherData!=null){
                    TextToSpeech.tts(weatherData);}
            }
        });
        add(soundButton);

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
