package io.swagger.client;

import java.util.concurrent.ThreadLocalRandom;
import io.swagger.client.model.LiftRide;

public class SkierLiftRideEvent {
    private int skierID;
    private int resortID;
    private int liftID;
    private int time;
    private LiftRide body;
    private String seasonID = "2024";
    private String dayID = "1";

    public SkierLiftRideEvent() {
        this.skierID = ThreadLocalRandom.current().nextInt(1, 100001);
        this.resortID = ThreadLocalRandom.current().nextInt(1, 11);
        this.liftID = ThreadLocalRandom.current().nextInt(1, 41);
        this.time = ThreadLocalRandom.current().nextInt(1, 361);
        this.body = new LiftRide(); 
        body.setTime(time);
        body.setLiftID(liftID);
    }

    // Getters and setters for each field if needed

    public int getSkierID() {
        return skierID;
    }

    public int getResortID() {
        return resortID;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public String getDayID() {
        return dayID;
    }

    public LiftRide getBody() {
        return body;
    }

    // Function to generate POST request data as a String
    public String generatePostData() {
        // Implement the format of POST request data as required.
        // Example: "skierID=123&resortID=5&liftID=20&seasonID=2024&dayID=1&time=120"
        return String.format("skierID=%d&resortID=%d&liftID=%d&seasonID=%d&dayID=%d&time=%d",
                             skierID, resortID, liftID, seasonID, dayID, time);
    }

    // Optionally, override toString method for easy printing
    @Override
    public String toString() {
        return generatePostData();
    }
}
