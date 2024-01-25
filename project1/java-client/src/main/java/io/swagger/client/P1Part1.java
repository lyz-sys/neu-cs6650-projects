package io.swagger.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.swagger.client.api.SkiersApi;

/**
 * P1Part1
 */
public class P1Part1 {
    private static final int MAX_EVENTS = 200000;
    private static final SkiersApi api = new SkiersApi(
        new ApiClient().setBasePath("http://35.167.101.115:8080/project1/")); // adjust the IP address as needed for your server

    public static void main(String[] args) {
        // The shared queue for events
        BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(MAX_EVENTS);

        // The event generator thread
        Thread eventGenerator = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                SkierLiftRideEvent event = new SkierLiftRideEvent();
                eventQueue.offer(event); // Non-blocking operation
            }
        });
        eventGenerator.start();

        // The posting threads
        for (int i = 0; i < 32; i++) {
            Thread postingThread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    SkierLiftRideEvent event = eventQueue.poll(); // Non-blocking operation, but you should handle the possibility of null
                    if (event != null) {
                        try { // todo: no success message
                            System.out.println("!!!!!" + event.getBody());
                            api.writeNewLiftRide(event.getBody(), event.getResortID(), event.getSeasonID(), event.getDayID(), event.getSkierID());
                            System.out.println("Success!");
                        } catch (ApiException e) {
                            System.err.println("Exception when calling SkiersApi#writeNewLiftRide");
                            e.printStackTrace();
                        }
                        // Handle the response, and immediately continue to the next iteration
                    }
                }
            });
            postingThread.start();
        }

        // Make sure to handle thread interruption, joining, and proper shutdown
    }
}