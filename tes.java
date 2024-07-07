import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageQueueApp {

    private static final int NUM_MESSAGES = 10;
    private static final int NUM_PRODUCERS = 2;
    private static final int NUM_CONSUMERS = 3;

    public static void main(String[] args) {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        AtomicInteger successfulMessages = new AtomicInteger(0);
        AtomicInteger errorMessages = new AtomicInteger(0);

        // Producers
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            new Thread(new Producer(queue, successfulMessages, errorMessages)).start();
        }

        // Consumers
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            new Thread(new Consumer(queue, successfulMessages, errorMessages)).start();
        }

        // Logging thread
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

    /**
     * Represents a message with content.
     */
    static class Message {
        private String content;

        public Message(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    /**
     * Producer class that produces messages and puts them into the queue.
     */
    static class Producer implements Runnable {
        private BlockingQueue<Message> queue;
        private AtomicInteger successfulMessages;
        private AtomicInteger errorMessages;

        public Producer(BlockingQueue<Message> queue, AtomicInteger successfulMessages, AtomicInteger errorMessages) {
            this.queue = queue;
            this.successfulMessages = successfulMessages;
            this.errorMessages = errorMessages;
        }

        @Override
        public void run() {
            for (int j = 0; j < NUM_MESSAGES; j++) {
                String messageContent = "Message-" + Thread.currentThread().getId() + "-" + j;
                Message message = new Message(messageContent);
                try {
                    queue.put(message);
                    System.out.println("Produced: " + messageContent);
                    successfulMessages.incrementAndGet(); // Increment successful message count
                } catch (InterruptedException e) {
                    System.err.println("Error while producing message: " + e.getMessage());
                    errorMessages.incrementAndGet(); // Increment error count
                }
            }
        }
    }

    /**
     * Consumer class that consumes messages from the queue.
     */
    static class Consumer implements Runnable {
        private BlockingQueue<Message> queue;
        private AtomicInteger successfulMessages;
        private AtomicInteger errorMessages;

        public Consumer(BlockingQueue<Message> queue, AtomicInteger successfulMessages, AtomicInteger errorMessages) {
            this.queue = queue;
            this.successfulMessages = successfulMessages;
            this.errorMessages = errorMessages;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Message message = queue.take();
                    System.out.println("Consumed: " + message.getContent());
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
}
