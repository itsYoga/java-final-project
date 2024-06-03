import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class TrayIconManager {

    private TrayIcon trayIcon;

    public TrayIconManager(JFrame frame) {
        // Check if the system supports the tray
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported.");
            return;
        }

        // Create the system tray instance
        SystemTray tray = SystemTray.getSystemTray();

        // Load the image for the tray icon
        ImageIcon icon = new ImageIcon("src/image/trayicon.png");
        Image image = icon.getImage();

        // Create a popup menu for the tray icon
        PopupMenu popup = new PopupMenu();
        MenuItem openItem = new MenuItem("Please put on your red nose before opening.");
            MenuItem exitItem = new MenuItem("Everything is so great that it makes me want to leave.");

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
                frame.setExtendedState(JFrame.NORMAL);
                trayIcon.displayMessage("IT IS IMPORTANT!!!",
                        "Oh! You forgot your clown mask!",
                        TrayIcon.MessageType.INFO);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trayIcon.displayMessage("WAIT!!!",
                        "You have to return the mask and red nose, those are MINE!!!", TrayIcon.MessageType.INFO);
                System.exit(0);
            }
        });

        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);

        // Create the tray icon
        trayIcon = new TrayIcon(image, "Yes, it is a Weather App", popup);
        trayIcon.setImageAutoSize(true);

        // Add action listener to tray icon
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    frame.setVisible(true);
                    frame.setExtendedState(JFrame.NORMAL);
                }
            }
        });

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void displayMessage(String caption, String text, TrayIcon.MessageType messageType) {
        trayIcon.displayMessage(caption, text, messageType);
    }
}
