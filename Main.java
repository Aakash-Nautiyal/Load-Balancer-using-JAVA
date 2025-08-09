import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {

    // Shared variable for delay (in milliseconds)
    private static volatile int delay = 1000;

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

            // Slider
            JSlider slider = new JSlider(100, 5000, delay); // min=100ms, max=5s
            slider.setMajorTickSpacing(1000);
            slider.setPaintTicks(true);
            slider.setBackground(matrixBlack);
            slider.setForeground(matrixGreen);
            slider.addChangeListener(e -> {
                delay = slider.getValue();
                label.setText("Request Delay: " + delay + " ms");
            });

            // Ticks and labels styled
            slider.setPaintLabels(true);
            slider.setLabelTable(slider.createStandardLabels(1000));
            java.util.Dictionary<Integer, JLabel> labelTable = slider.getLabelTable();
            for (java.util.Enumeration<Integer> keys = labelTable.keys(); keys.hasMoreElements();) {
                Integer key = keys.nextElement();
                JLabel lbl = labelTable.get(key);
                if (lbl != null) {
                    lbl.setForeground(matrixGreen);
                }
            }

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
