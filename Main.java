import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {

    // Shared variable for delay (in milliseconds)
    private static volatile int delay = 100; // Default start delay (in ms)

    public static void main(String[] args) {

        // ====== Matrix Theme UI for Slider ======
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Request Delay Controller");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 150);
            frame.setLayout(new BorderLayout());

            // Matrix colors
            Color matrixGreen = new Color(0, 255, 70);
            Color matrixBlack = Color.BLACK;

            // Label
            JLabel label = new JLabel("Request Delay: " + delay + " ms", SwingConstants.CENTER);
            label.setForeground(matrixGreen);
            label.setFont(new Font("Consolas", Font.BOLD, 18));
            label.setBackground(matrixBlack);
            label.setOpaque(true);

            // Slider with new range: 1 ms to 500 ms
            JSlider slider = new JSlider(1, 500, delay); // min=1ms, max=500ms
            slider.setMajorTickSpacing(100);
            slider.setMinorTickSpacing(10);
            slider.setPaintTicks(true);
            slider.setBackground(matrixBlack);
            slider.setForeground(matrixGreen);
            slider.addChangeListener(e -> {
                delay = slider.getValue();
                label.setText("Request Delay: " + delay + " ms");
            });

            // Custom labels for the slider
            slider.setPaintLabels(true);
            java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
            labelTable.put(1, new JLabel("1"));
            labelTable.put(100, new JLabel("100"));
            labelTable.put(200, new JLabel("200"));
            labelTable.put(300, new JLabel("300"));
            labelTable.put(400, new JLabel("400"));
            labelTable.put(500, new JLabel("500"));

            for (JLabel lbl : labelTable.values()) {
                lbl.setForeground(matrixGreen);
            }
            slider.setLabelTable(labelTable);

            frame.add(label, BorderLayout.NORTH);
            frame.add(slider, BorderLayout.CENTER);

            frame.getContentPane().setBackground(matrixBlack);
            frame.setVisible(true);
        });

        // ====== Thread 1: Sending requests ======
        Thread senderThread = new Thread(() -> {
            String host = "localhost";
            int port = 8081;

            while (true) {
                try (Socket socket = new Socket(host, port);
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    UserRequest request = new UserRequest();
                    out.writeObject(request);

                } catch (Exception e) {
                    System.out.println("Error sending object: " + e.getMessage());
                }

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        senderThread.start();
    }
}
