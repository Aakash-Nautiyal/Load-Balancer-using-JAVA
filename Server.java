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

    public int getPendingRequests(){
        return serverQueue.size();
    }

    public int getServerId(){
        return id;
    }

    @Override
    public void run() {
        while (running) {
            try {
                UserRequest req = serverQueue.take();
                System.out.println("Server " + id + " processing: " + req);

                // Simulate more realistic processing time:
                // Base time + variable time (non-linear scaling) + random jitter
                int baseTime = 50; // minimum time in ms
                int variableTime = (int) Math.pow(req.getRequestSize(), 1.5); // more dramatic growth
                int jitter = (int) (Math.random() * 200); // ±200 ms random delay

                Thread.sleep(baseTime + variableTime + jitter);

            } catch (InterruptedException e) {
                running = false; // Exit gracefully
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
