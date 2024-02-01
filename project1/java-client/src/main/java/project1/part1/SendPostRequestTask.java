package project1.part1;

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
import io.swagger.client.ApiClient;
import project1.SkierLiftRideEvent;

public class SendPostRequestTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(SendPostRequestTask.class);
    private int numOfRequests;
    private BlockingQueue<SkierLiftRideEvent> eventQueue;
    private int successfulCount = 0;
    private int unsuccessfulCount = 0;
    private final SkiersApi api = new SkiersApi(
        new ApiClient().setBasePath("http://34.220.101.212:8080/project1/"));

    public SendPostRequestTask(int numOfRequests, BlockingQueue<SkierLiftRideEvent> eventQueue) {
        this.numOfRequests = numOfRequests;
        this.eventQueue = eventQueue;
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public int getUnsuccessfulCount() {
        return unsuccessfulCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < numOfRequests; i++) {
            processEvent();
        }
    }

    private void processEvent() {
        SkierLiftRideEvent event = eventQueue.poll();
        if (event == null) {
            handleEmptyQueue();
            return;
        }

        boolean success = sendRequestsUntilSuccessful(event);
        updateCountsAndLog(success);
    }

    private boolean sendRequestsUntilSuccessful(SkierLiftRideEvent event) {
        for (int j = 0; j < 5; j++) {
            if (attemptPostRequest(api, event)) {
                return true;
            }
        }
        return false;
    }

    private void handleEmptyQueue() {
        logger.info("not enough events in the queue");
        try {
            Thread.sleep(100); // Wait for a while before retrying
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }

    private void updateCountsAndLog(boolean success) {
        if (success) {
            successfulCount++;
            logger.info("Success!");
        } else {
            unsuccessfulCount++;
            logger.info("Received non-success response or ApiException occurred");
        }
    }

    private boolean attemptPostRequest(SkiersApi api, SkierLiftRideEvent event) {
        try {
            ApiResponse<Void> response = api.writeNewLiftRideWithHttpInfo(
                    event.getBody(), event.getResortID(), event.getSeasonID(), event.getDayID(), event.getSkierID());
            return response.getStatusCode() == 201;
        } catch (ApiException e) {
            handleApiException(e);
            return false;
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
}