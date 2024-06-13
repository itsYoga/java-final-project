import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.SecurityException;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
public class InfoReadWriter {
    private static Formatter output;
    private String fileName;
    private static List<String> lines;

    public static void start(int subCurrentIndex, JTextField searchTextField) {
       loadFile("subscribe.txt");
        if (!lines.isEmpty()) {
            subCurrentIndex = 0;
            searchTextField.setText(lines.get(subCurrentIndex));
        }
    }

    public void addInfo(String Name) {
        openFile();
        addRecord(Name);
        closeFile();
    }

    public void openFile() {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            output = new Formatter(fw);
        } catch (SecurityException securityException) {
            System.err.println("Write permission denied. Terminating.");
            System.exit(1); // terminate the program
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error opening file. Terminating.");
            System.exit(1); // terminate the program
        } catch (IOException e) {
            System.err.println("I/O error. Terminating.");
            System.exit(1); // terminate the program
        }
    }

    // add records to file
    public void addRecord(String Name) {
        try {
            // output new record to file; assumes valid input
            FileWriter fw = new FileWriter(new File(fileName), true);
            fw.write(String.format("%s\n", Name));
            fw.close();
        } catch (FormatterClosedException formatterClosedException) {
            System.err.println("Error writing to file. Terminating.");
        }catch (IOException e){
            System.err.println("Error writing to file. Terminating.");
        }
    }

    // close file
    public static void closeFile() {
        if (output != null)
            output.close();
    }
    private static void loadFile(String fileName) {
        lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showPreviousLine(int currentIndex, JTextField searchTextField) {
        if (currentIndex > 0) {
            currentIndex--;
            searchTextField.setText(lines.get(currentIndex));
        }
    }

    public static void showNextLine(int currentIndex, JTextField searchTextField) {
        if (currentIndex < lines.size() - 1) {
            currentIndex++;
            searchTextField.setText(lines.get(currentIndex));
        }
    }
}