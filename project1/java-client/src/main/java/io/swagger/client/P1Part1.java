package io.swagger.client;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import io.swagger.client.api.SkiersApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * P1Part1
 */
public class P1Part1 {
    private static final Logger logger = LogManager.getLogger(P1Part1.class);

    private static final int MAX_EVENTS = 200000;
    private static final SkiersApi api = new SkiersApi(
            new ApiClient().setBasePath("http://35.91.207.31:8080/project1/")); // adjust the IP address as needed for
                                                                                // your server

    public static void main(String[] args) {
        // The shared queue for events
        BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(MAX_EVENTS);
        List<Thread> threadList = new ArrayList<>();

        // Generate 200,000 events
        for (int i = 0; i < MAX_EVENTS; i++) {
            SkierLiftRideEvent event = new SkierLiftRideEvent();
            eventQueue.offer(event); // Non-blocking operation
        }

        // Initiate 32 threads to post events for task 1
        for (int i = 0; i < 32; i++) {
            Thread postingThread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    SkierLiftRideEvent event = eventQueue.poll(); // Non-blocking operation, but you should handle the
                                                                  // possibility of null
                    if (event != null) {
                        try {
                            boolean success = false;
                            ApiResponse<Void> response = null;
                            for (int k = 0; k < 5; k++) {
                                // Make the POST request and receive the response
                                response = api.writeNewLiftRideWithHttpInfo(
                                        event.getBody(), event.getResortID(), event.getSeasonID(), event.getDayID(),
                                        event.getSkierID());
                                // Check the response code
                                if (response.getStatusCode() == 201) {
                                    success = true;
                                    break;
                                }
                            }

                            if (success) {
                                logger.info("Success! Received response code: " + response.getStatusCode());
                            } else {
                                logger.info("Received non-success response code: " + response.getStatusCode());
                            }
                        } catch (ApiException e) {
                            logger.info("Exception when calling SkiersApi#writeNewLiftRide");
                            e.printStackTrace();
                        }
                        // Handle the response, and immediately continue to the next iteration
                    } else {
                        logger.info("not enough events in the queue");

                        // Handle the possibility of null, such as waiting or retrying
                        try {
                            Thread.sleep(100); // Wait for a while before retrying
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // Restore the interrupted status
                            break; // Exit the loop if the thread is interrupted
                        }
                    }
                }
            });
            threadList.add(postingThread);
            postingThread.start();
        }

        // Make sure to handle thread interruption, joining, and proper shutdown
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                break; // Exit the loop if the thread is interrupted
            }
        }
    }
}