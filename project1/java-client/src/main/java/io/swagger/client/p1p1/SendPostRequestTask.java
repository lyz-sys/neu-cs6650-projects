package io.swagger.client.p1p1;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import io.swagger.client.api.SkiersApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;

public class SendPostRequestTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(SendPostRequestTask.class);
    private final SkiersApi api;
    private int numOfRequests;
    private BlockingQueue<SkierLiftRideEvent> eventQueue;
    private int successfulCount = 0;
    private int unsuccessfulCount = 0;

    public SendPostRequestTask(int numOfRequests, BlockingQueue<SkierLiftRideEvent> eventQueue, SkiersApi api) {
        this.numOfRequests = numOfRequests;
        this.eventQueue = eventQueue;
        this.api = api;
    }

    @Override
    public void run() {
        for (int j = 0; j < numOfRequests; j++) {
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
                        successfulCount++;
                        logger.info("Success! Received response code: " + response.getStatusCode());
                    } else {
                        unsuccessfulCount++;
                        logger.info("Received non-success response code: " + response.getStatusCode());
                    }
                } catch (ApiException e) {
                    logger.info("Exception when calling SkiersApi#writeNewLiftRide");
                    handleApiException(e);

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
    }

    private static void handleApiException(ApiException e) {
        logger.error("API Exception occurred: " + e.getMessage());

        // Example: Check HTTP status code
        int statusCode = e.getCode();
        switch (statusCode) {
            case 400: // Bad Request
                // Specific handling for bad request
                break;
            case 500: // Internal Server Error
                // Retry logic or other handling
                break;
            default:
                // General handling for other codes
        }

        // Log additional details if available
        logger.info(statusCode);
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public int getUnsuccessfulCount() {
        return unsuccessfulCount;
    }
}