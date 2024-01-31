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
import io.swagger.client.ApiClient;

/**
 * Driver class for Part 1 of Project 1.
 */
public class Driver {
    private static final Logger logger = LogManager.getLogger(Driver.class);
    private static final int MAX_EVENTS = 200000;
    private static int successfulCount = 0;
    private static int unsuccessfulCount = 0;
    private static BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(MAX_EVENTS);
    private static List<Thread> threadList = new ArrayList<>();
    private static List<SendPostRequestTask> sendPostRequestTaskList = new ArrayList<>();
    private static final SkiersApi api = new SkiersApi(
            new ApiClient().setBasePath("http://35.86.109.117:8080/project1/"));

    public static void main(String[] args) {
        long startTime;
        long endTime;

        // // warm up throuput testing
        // for (int i = 0; i < 10000; i++) {
        // SkierLiftRideEvent event = new SkierLiftRideEvent();
        // eventQueue.offer(event); // Non-blocking operation
        // }

        // int numOfRequests = 10000;
        // startTime = System.currentTimeMillis();
        // Thread t = new Thread(new SendPostRequestTask(numOfRequests, eventQueue,
        // api));
        // t.start();
        // try {
        // t.join();
        // } catch (InterruptedException e) {
        // Thread.currentThread().interrupt();
        // }
        // endTime = System.currentTimeMillis();
        // logger.info("Little's Law: N = X * R, N = 10000, R = 1, X = {}", endTime -
        // startTime);

        // Generate 200,000 events
        for (int i = 0; i < MAX_EVENTS; i++) {
            SkierLiftRideEvent event = new SkierLiftRideEvent();
            eventQueue.offer(event); // Non-blocking operation
        }

        startTime = System.currentTimeMillis();

        // task 1
        for (int i = 0; i < 32; i++) {
            SendPostRequestTask task = new SendPostRequestTask(1000, eventQueue, api);
            Thread postingThread = new Thread(task);
            threadList.add(postingThread);
            sendPostRequestTaskList.add(task);
            postingThread.start();
        }

        // task 2
        for (int i = 0; i < 168; i++) {
            SendPostRequestTask task = new SendPostRequestTask(1000, eventQueue, api);
            Thread postingThread = new Thread(task);
            threadList.add(postingThread);
            sendPostRequestTaskList.add(task);
            postingThread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                break; // Exit the loop if the thread is interrupted
            }
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

        // Calculate total run time
        endTime = System.currentTimeMillis();
        double wallTimeInSeconds = (endTime - startTime) / 1000.0;

        logger.info("Total run time: {} s", wallTimeInSeconds);
        logger.info("Total throughput in requests per second: {}", successfulCount /
                wallTimeInSeconds);
    }
}
