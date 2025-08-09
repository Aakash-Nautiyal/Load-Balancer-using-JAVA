import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends Thread {
    private final int id;
    private final BlockingQueue<UserRequest> serverQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true; // flag for safe shutdown

    public Server(int id) {
        super("Server-" + id); // name the thread
        this.id = id;
    }

    public void addRequest(UserRequest request) throws InterruptedException {
        serverQueue.put(request);  // send request to this server
    }

    @Override
    public void run() {
        while (running) {
            try {
                UserRequest req = serverQueue.take();
                System.out.println("Server " + id + " processing: " + req);
                Thread.sleep(req.getRequestSize() * 10); // simulate work
            } catch (InterruptedException e) {
                // Thread was interrupted — time to exit
                running = false;
            }
        }
        System.out.println("Server " + id + " stopped.");
    }

    // Safe shutdown method
    public void shutdown() {
        running = false;
        this.interrupt();
    }
}
