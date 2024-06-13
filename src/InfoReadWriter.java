import javax.swing.*;
import java.util.*;
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
    private static String fileName;
    private static List<String> lines;

    public static void start(int subCurrentIndex, JTextField searchTextField) {
       fileName="subscribe.txt";
    }

    public static void addInfo(String Name) {
        openFile();
        addRecord(Name);
        closeFile();
    }
    public static void removeInfo(String Name) {
        openFile();
        removeRecord(Name);;
        closeFile();
    }

    public static void openFile() {
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

    public static boolean checkInfo(String name) {
        try {
            Set<String> records = new HashSet<>();
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line.trim());
            }
            reader.close();
            return records.contains(name);
        } catch (IOException e) {
            System.err.println("Error reading file. Terminating.");
            e.printStackTrace();
            return false;
        }
    }

    // Add record to file
    public static void addRecord(String name) {
        try {
            if (checkInfo(name)) {
                JOptionPane.showMessageDialog(null, "已在訂閱清單，無法再次加入", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            FileWriter fw = new FileWriter(new File(fileName), true);
            fw.write(String.format("%s%n", name));
            fw.close();
            JOptionPane.showMessageDialog(null, "成功訂閱", "success", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            System.err.println("Error reading or writing to file. Terminating.");
            e.printStackTrace();
        }
    }

    // Remove record from file
    public static void removeRecord(String name) {
        try {
            List<String> records = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals(name)) {
                    records.add(line);
                }
            }
            reader.close();

            FileWriter fw = new FileWriter(fileName);
            for (String record : records) {
                fw.write(String.format("%s%n", record));
            }
            fw.close();

            JOptionPane.showMessageDialog(null, "成功取消訂閱", "success", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            System.err.println("Error reading or writing to file. Terminating.");
            e.printStackTrace();
        }
    }


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

    public static boolean showPreviousLine(int currentIndex, JTextField searchTextField) {
        if (currentIndex >= 0) {
            loadFile("subscribe.txt");
            searchTextField.setText(lines.get(currentIndex));
            return true;
        }else return false;

    }

    public static boolean showNextLine(int currentIndex, JTextField searchTextField) {
        if (currentIndex <= lines.size() - 1) {
            loadFile("subscribe.txt");
            searchTextField.setText(lines.get(currentIndex));
            return true;
        }else return false;

    }
}