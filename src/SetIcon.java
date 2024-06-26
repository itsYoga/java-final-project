import javax.swing.*;
import java.awt.*;
public class SetIcon {

    public static void setIcon(JLabel icon, String weatherCondition) {
        if(weatherCondition.contains("晴") && weatherCondition.contains("雨")) {
            if(weatherCondition.contains("雷"))
                icon.setIcon(loadImage("src/image/sunny_thunderstorm.png"));
            else icon.setIcon(loadImage("src/image/sunny_rain.png"));
        } else if (weatherCondition.contains("晴")) {
            icon.setIcon(loadImage("src/image/clear.png"));
        } else if(weatherCondition.contains("雷雨")) {
            icon.setIcon(loadImage("src/image/rain_thunder.png"));
        } else if (weatherCondition.contains("雨")) {
            icon.setIcon(loadImage("src/image/rain.png"));
        } else if(weatherCondition.contains("霧")) {
            icon.setIcon(loadImage("src/image/cloudy_fog.png"));
        } else if (weatherCondition.contains("多雲") || weatherCondition.contains("陰")) {
            icon.setIcon(loadImage("src/image/cloudy.png"));
        } else if (weatherCondition.contains("雪")) {//useless haha
            icon.setIcon(loadImage("src/image/snow.png"));
        }
    }


    private static ImageIcon loadImage(String path) {
        return new ImageIcon(path);
    }

    public static void modifyIcon(JLabel icon, int scaledWidth, int scaledHeight) {

        Icon originalIcon = icon.getIcon();
        ImageIcon imageIcon = new ImageIcon(((ImageIcon) originalIcon).getImage());
        Image image = imageIcon.getImage();

        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        icon.setIcon(scaledIcon);
    }
}
