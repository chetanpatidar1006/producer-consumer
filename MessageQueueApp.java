import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class simulates a message-driven application using a producer-consumer pattern
 * with a message queue. It tracks and logs the total number of messages processed
 * successfully and the number of errors encountered.
 */
public class MessageQueueApp {

    private static final int NUM_PRODUCERS = 2;
    private static final int NUM_CONSUMERS = 3;

    public static void main(String[] args) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        AtomicInteger successfulMessages = new AtomicInteger(0);
        AtomicInteger errorMessages = new AtomicInteger(0);

        // Start producers
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            new Thread(new Producer(queue)).start();
        }

        // Start consumers
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            new Thread(new Consumer(queue, successfulMessages, errorMessages)).start();
        }

        // Start logging thread to print statistics
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

        // Accept user input and produce messages
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter messages to produce (type 'exit' to stop):");
        String input;
        while (!(input = scanner.nextLine()).equals("exit")) {
            try {
                queue.put(input);
                System.out.println("Produced: " + input);
            } catch (InterruptedException e) {
                System.err.println("Error while producing message: " + e.getMessage());
            }
        }
        scanner.close();
    }

    /**
     * Consumer class that consumes messages from the queue.
     */
    static class Consumer implements Runnable {
        private BlockingQueue<String> queue;
        private AtomicInteger successfulMessages;
        private AtomicInteger errorMessages;

        public Consumer(BlockingQueue<String> queue, AtomicInteger successfulMessages, AtomicInteger errorMessages) {
            this.queue = queue;
            this.successfulMessages = successfulMessages;
            this.errorMessages = errorMessages;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String message = queue.take();
                    System.out.println("Consumed: " + message);
                    // Simulate processing
                    Thread.sleep(100); // Processing time simulation
                    successfulMessages.incrementAndGet(); // Increment successful message count
                } catch (InterruptedException e) {
                    System.err.println("Error while consuming message: " + e.getMessage());
                    errorMessages.incrementAndGet(); // Increment error count
                }
            }
        }
    }

    /**
     * Producer class that accepts user input and puts messages into the queue.
     */
    static class Producer implements Runnable {
        private BlockingQueue<String> queue;

        public Producer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            // Producer doesn't need to produce messages independently in this version
            // It just waits for user input and puts messages into the queue
        }
    }
}
