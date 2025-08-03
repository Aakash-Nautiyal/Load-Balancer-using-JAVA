import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {


    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Load Balancer running on port 8081...");

        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                UserRequest request = (UserRequest) in.readObject();
                System.out.println("Received: " +ConsoleColor.BLUE+request);

            } catch (Exception e) {
                System.out.println("Error receiving object: " + e.getMessage());
            }
        }
    }
}
