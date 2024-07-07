import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

// Main application class for simulating a producer-consumer scenario using a message queue.
public class MessageQueueApp {

    private static final int NUM_MESSAGES = 10; // Number of messages each producer produces
    private static final int NUM_PRODUCERS = 2; // Number of producer threads
    private static final int NUM_CONSUMERS = 3; // Number of consumer threads

  
    public static void main(String[] args) {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<>(); // Message queue
        AtomicInteger successfulMessages = new AtomicInteger(0); // Counter for successful messages processed
        AtomicInteger errorMessages = new AtomicInteger(0); // Counter for errors encountered

        // Start producer threads
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            new Thread(() -> {
                for (int j = 0; j < NUM_MESSAGES; j++) {
                    String messageContent = "Message-" + i + "-" + j;
                    Message message = new Message(messageContent);
                    try {
                        queue.put(message); // Put message into the queue
                        System.out.println("Produced: " + messageContent);
                        successfulMessages.incrementAndGet(); // Increment successful message counter
                    } catch (InterruptedException e) {
                        System.err.println("Error while producing message: " + e.getMessage());
                        errorMessages.incrementAndGet(); // Increment error counter
                    }
                }
            }).start();
        }

        // Start consumer threads
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        Message message = queue.take(); // Take message from the queue
                        System.out.println("Consumed: " + message.getContent());
                        // Simulate processing
                        Thread.sleep(100); // Processing time simulation
                        successfulMessages.incrementAndGet(); // Increment successful message counter
                    } catch (InterruptedException e) {
                        System.err.println("Error while consuming message: " + e.getMessage());
                        errorMessages.incrementAndGet(); // Increment error counter
                    }
                }
            }).start();
        }

        // Logging thread to periodically print statistics
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Log every 5 seconds
                    int successCount = successfulMessages.get();
                    int errorCount = errorMessages.get();
                    System.out.println("\nMessages processed successfully: " + successCount);
                    System.out.println("Errors encountered: " + errorCount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
