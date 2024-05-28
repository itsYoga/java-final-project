import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class MjpegStreamPlayer {
    public static void play(String STREAM_URL) {
        JFrame frame = new JFrame("MJPEG Stream Player");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(640, 480);

        JLabel label = new JLabel();
        frame.add(label);
        frame.setVisible(true);

        new Thread(() -> {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(STREAM_URL)) {
                grabber.start();
                Java2DFrameConverter converter = new Java2DFrameConverter();

                while (true) {
                    Frame grabbedFrame = grabber.grab();
                    if (grabbedFrame == null) {
                        break;
                    }

                    BufferedImage bufferedImage = converter.convert(grabbedFrame);
                    if (bufferedImage != null) {
                        SwingUtilities.invokeLater(() -> {
                            ImageIcon icon = new ImageIcon(bufferedImage);
                            label.setIcon(icon);
                        });
                    }
                }
                grabber.stop();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
