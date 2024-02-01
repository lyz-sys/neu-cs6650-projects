package project1.part2;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.ApiException;
import io.swagger.client.ApiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project1.SkierLiftRideEvent;
import project1.part2.SendPostRequestTask;

/**
 * Driver class for Part 2 of Project 1.
 */
public class Driver {
    private static final int MAX_EVENTS = 200000;
    private static final Logger logger = LogManager.getLogger(Driver.class);
    private static int successfulCount = 0;
    private static int unsuccessfulCount = 0;
    private static BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(200000);
    private static List<SendPostRequestTask> sendPostRequestTaskList = new ArrayList<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Generate 200,000 events
        for (int i = 0; i < MAX_EVENTS; i++) {
            SkierLiftRideEvent event = new SkierLiftRideEvent();
            eventQueue.offer(event); // Non-blocking operation
        }

        ExecutorService executor = Executors.newFixedThreadPool(168);
        for (int i = 0; i < 168; i++) {
            SendPostRequestTask task = new SendPostRequestTask(1000, eventQueue);
            sendPostRequestTaskList.add(task);
            executor.submit(task);
        }
        // Shut down the executor service
        executor.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Optional: Cancel currently executing tasks
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
        
        // Wait for all threads to finish and collect results
        for (SendPostRequestTask task : sendPostRequestTaskList) {
            try {
                successfulCount += task.getSuccessfulCount();
                unsuccessfulCount += task.getUnsuccessfulCount();
            } catch (Exception e) {
                // Handle possible exceptions
            }
        }

        logger.info("Successful requests: {}", successfulCount);
        logger.info("Unsuccessful requests: {}", unsuccessfulCount);

        double wallTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.0;

        logger.info("Total run time: {} second", wallTimeInSeconds);
        logger.info("Total throughput: {} requests/second", successfulCount /
                wallTimeInSeconds);
    }

    // private void writeToCSV() {
    //     try (PrintWriter writer = new PrintWriter(new File("results.csv"))) {
    //         StringBuilder sb = new StringBuilder();
    //         sb.append("StartTime,RequestType,Latency,ResponseCode\n");
    
    //         for (RequestResult result : requestResults) {
    //             sb.append(System.currentTimeMillis()).append(",");
    //             sb.append("POST").append(",");
    //             sb.append(result.getLatency()).append(",");
    //             sb.append(result.getStatusCode()).append("\n");
    //         }
    
    //         writer.write(sb.toString());
    //     } catch (FileNotFoundException e) {
    //         System.out.println(e.getMessage());
    //     }
    // }    
}
