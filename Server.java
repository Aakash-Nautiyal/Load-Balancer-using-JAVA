import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents a server thread that processes UserRequest objects from its own queue.
 * Each server tracks:
 * - Current active + queued requests
 * - Total processed requests
 * - Real-time load percentage
 */
public class Server extends Thread {

    // ====== Server Identification ======
    private final int id;

    // ====== Request Queue ======
    private final BlockingQueue<UserRequest> serverQueue = new LinkedBlockingQueue<>();

    // ====== Thread Control ======
    private volatile boolean running = true;

    // ====== Metrics ======
    private volatile int currentConnections = 0;    // Active + queued
    private volatile int totalProcessedRequests = 0;
    private volatile int processingRequests = 0;    // Requests currently being processed

    // ====== Config ======
    private static final int MAX_CAPACITY = 50;     // Max active+queued requests for 100% load

    /**
     * Creates a server thread with the given ID.
     */
    public Server(int id) {
        super("Server-" + id);
        this.id = id;
    }

    /**
     * Adds a request to this server's queue.
     */
    public void addRequest(UserRequest request) throws InterruptedException {
        serverQueue.put(request);
        updateCurrentConnections(); // increment pending count
    }

    /**
     * Returns number of pending requests (active + queued).
     */
    public int getPendingRequests() {
        return currentConnections;
    }

    /**
     * Returns the server ID.
     */
    public int getServerId() {
        return id;
    }

    /**
     * Returns total active connections (active + queued).
     */
    public int getCurrentConnections() {
        return currentConnections;
    }

    /**
     * Returns total number of requests processed by this server.
     */
    public int getTotalProcessedRequests() {
        return totalProcessedRequests;
    }

    /**
     * Returns real-time load percentage (0–100%).
     * Based on MAX_CAPACITY.
     */
    public double getLoadPercent() {
        return Math.min(100.0, (currentConnections / (double) MAX_CAPACITY) * 100);
    }

    /**
     * Main server loop — takes requests from queue and processes them.
     */
    @Override
    public void run() {
        while (running) {
            try {
                // Take next request (blocking)
                UserRequest req = serverQueue.take();
                processingRequests++;
                updateCurrentConnections();

                System.out.println("Server " + id + " processing: " + req);

                // Simulate processing time
                int baseTime = 50;
                int variableTime = (int) Math.pow(req.getRequestSize(), 1.5);
                int jitter = (int) (Math.random() * 200);
                long processingTime = baseTime + variableTime + jitter;

                Thread.sleep(processingTime);

                // Update metrics
                totalProcessedRequests++;

                processingRequests--;
                updateCurrentConnections();

            } catch (InterruptedException e) {
                running = false; // Thread interrupted — stop loop
            }
        }
        System.out.println("Server " + id + " stopped.");
    }

    /**
     * Updates currentConnections = queued + active.
     */
    private void updateCurrentConnections() {
        currentConnections = serverQueue.size() + processingRequests;
    }

    /**
     * Stops the server thread gracefully.
     */
    public void shutdown() {
        running = false;
        this.interrupt();
    }
}
