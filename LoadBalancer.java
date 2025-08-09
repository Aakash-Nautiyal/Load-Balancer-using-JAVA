import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class LoadBalancer {

    public static final BlockingQueue<UserRequest> requestQueue = new LinkedBlockingQueue<>();
    public static final List<Server> serverList = new CopyOnWriteArrayList<>();
    public static String currentAlgorithm = "RoundRobin";
    public static int totalRequests = 0;

    // UI Components (global so we can update them from anywhere)
    private static JLabel lblAlgorithm;
    private static JLabel lblTotalServers;
    private static JLabel lblTotalRequests;

    public static void main(String[] args) throws Exception {

        // Start server 1 by default
        Server s1 = new Server(1);
        s1.start();
        serverList.add(s1);

        // Start dispatcher thread
        startDispatcherThread();

        // Start the Swing UI in another thread
        SwingUtilities.invokeLater(LoadBalancer::createAndShowUI);

        // Socket server
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Load Balancer running on port 8081...");

        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                UserRequest request = (UserRequest) in.readObject();

                if (request.getIP() == null || request.getRequestSize() == 0) {
                    continue;
                }

                requestQueue.add(request);

            } catch (Exception e) {
                System.out.println("Error receiving object: " + e.getMessage());
            }
        }
    }

    public static void startDispatcherThread() {
        new Thread(() -> {
            int index = 0;
            while (true) {
                try {
                    UserRequest request = requestQueue.take();

                    if (serverList.isEmpty()) {
                        System.out.println("No available servers. Dropping request: " + request);
                        continue;
                    }

                    Server server = serverList.get(index % serverList.size());
                    server.addRequest(request);

                    totalRequests++;
                    updateUILabels();

                    System.out.println("Dispatcher sent request to Server " + (index % serverList.size()));
                    index++;

                } catch (InterruptedException e) {
                    System.out.println("Dispatcher interrupted.");
                    break;
                } catch (Exception e) {
                    System.out.println("Dispatcher error: " + e.getMessage());
                }
            }
        }, "Dispatcher").start();
    }

    // ================= UI CODE =================
    private static void createAndShowUI() {
        JFrame frame = new JFrame("Load Balancer Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new GridLayout(0, 1));
        frame.getContentPane().setBackground(Color.BLACK);

        Font font = new Font("Consolas", Font.BOLD, 16);
        Color green = new Color(0, 255, 70);

        JButton btnAddServer = new JButton("Add Server");
        JButton btnRemoveServer = new JButton("Remove Server");
        JButton btnShowAlgorithm = new JButton("Show Current Algorithm");
        JButton btnChangeAlgorithm = new JButton("Change Algorithm");

        lblAlgorithm = new JLabel("Current Algorithm: " + currentAlgorithm);
        lblTotalServers = new JLabel("Total Servers: " + serverList.size());
        lblTotalRequests = new JLabel("Total Requests Made: " + totalRequests);

        for (JButton btn : new JButton[]{btnAddServer, btnRemoveServer, btnShowAlgorithm, btnChangeAlgorithm}) {
            styleButton(btn, font, green);
        }

        for (JLabel lbl : new JLabel[]{lblAlgorithm, lblTotalServers, lblTotalRequests}) {
            lbl.setForeground(green);
            lbl.setBackground(Color.BLACK);
            lbl.setFont(font);
        }

        btnAddServer.addActionListener(e -> {
            Server newServer = new Server(serverList.size() + 1);
            serverList.add(newServer);
            newServer.start();
            updateUILabels();
        });

        btnRemoveServer.addActionListener(e -> {
            if (!serverList.isEmpty()) {
                Server s = serverList.remove(serverList.size() - 1);
                s.shutdown();
                updateUILabels();
            }
        });

        btnShowAlgorithm.addActionListener(e -> {
            UIManager.put("OptionPane.background", Color.BLACK);
            UIManager.put("Panel.background", Color.BLACK);
            UIManager.put("OptionPane.messageForeground", new Color(0, 255, 70));
            UIManager.put("Button.background", Color.BLACK);
            UIManager.put("Button.foreground", new Color(0, 255, 70));
            UIManager.put("OptionPane.font", new Font("Consolas", Font.BOLD, 16));

            JOptionPane.showMessageDialog(
                    frame,
                    "Current Algorithm: " + currentAlgorithm,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });


        btnChangeAlgorithm.addActionListener(e -> {
            // Apply Matrix theme to dialog
            UIManager.put("OptionPane.background", Color.BLACK);
            UIManager.put("Panel.background", Color.BLACK);
            UIManager.put("OptionPane.messageForeground", new Color(0, 255, 70));
            UIManager.put("Button.background", Color.BLACK);
            UIManager.put("Button.foreground", new Color(0, 255, 70));
            UIManager.put("OptionPane.font", new Font("Consolas", Font.BOLD, 16));
            UIManager.put("ComboBox.background", Color.BLACK);
            UIManager.put("ComboBox.foreground", new Color(0, 255, 70));

            String[] algos = {"RoundRobin", "LeastConnections"};
            String choice = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Algorithm:",
                    "Change Algorithm",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    algos,
                    currentAlgorithm
            );
            if (choice != null) {
                currentAlgorithm = choice;
                updateUILabels();
            }
        });


        frame.add(btnAddServer);
        frame.add(btnRemoveServer);
        frame.add(btnShowAlgorithm);
        frame.add(btnChangeAlgorithm);
        frame.add(lblAlgorithm);
        frame.add(lblTotalServers);
        frame.add(lblTotalRequests);

        frame.setVisible(true);
    }

    private static void styleButton(JButton btn, Font font, Color green) {
        btn.setFont(font);
        btn.setBackground(Color.BLACK);
        btn.setForeground(green);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(green));
    }

    private static void updateUILabels() {
        SwingUtilities.invokeLater(() -> {
            lblAlgorithm.setText("Current Algorithm: " + currentAlgorithm);
            lblTotalServers.setText("Total Servers: " + serverList.size());
            lblTotalRequests.setText("Total Requests Made: " + totalRequests);
        });
    }
}
