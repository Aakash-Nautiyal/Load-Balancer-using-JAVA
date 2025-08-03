import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    // Shared variable for delay (in milliseconds)
    private static volatile int delay = 1000;

    public static void main(String[] args) {

        // Thread 1: Sending requests
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

        // Thread 2: Listening for user input to change delay
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter new delay in ms (e.g., 500 or 2000): ");
                try {
                    int newDelay = Integer.parseInt(scanner.nextLine());
                    if (newDelay >= 0) {
                        delay = newDelay;
                        System.out.println("Updated delay to " + delay + " ms.");
                    } else {
                        System.out.println("Please enter a non-negative number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                }
            }
        });

        senderThread.start();
        inputThread.start();
    }
}

