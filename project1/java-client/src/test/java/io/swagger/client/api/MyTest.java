/*
 * Ski Data API for NEU Seattle distributed systems course
 * An API for an emulation of skier managment system for RFID tagged lift tickets. Basis for CS6650 Assignments for 2019
 *
 * OpenAPI spec version: 2.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.api;

import io.swagger.client.model.LiftRide;
import io.swagger.client.model.ResponseMsg;
import io.swagger.client.model.SkierVertical;
import org.junit.Test;
import org.junit.Ignore;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.P1Part1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for SkiersApi
 */
public class MyTest {
    /**
     * write a new lift ride for the skier
     *
     * Stores new lift ride details in the data store
     *
     * @throws Exception
     *                   if the Api call fails
     */
    @Ignore
    @Test
    public void writeNewLiftRideTest() throws Exception {
        SkiersApi api = new SkiersApi(new ApiClient().setBasePath("http://35.91.207.31:8080/project1/"));

        LiftRide body = new LiftRide(); // LiftRide | Specify new Season value
        Integer resortID = 56; // Integer | ID of the resort the skier is at
        String seasonID = "seasonID_example"; // String | ID of the ski season
        String dayID = "100"; // String | ID number of ski day in the ski season
        Integer skierID = 56; // Integer | ID of the skier riding the lift
        try {
            ApiResponse<Void> response = api.writeNewLiftRideWithHttpInfo(body, resortID, seasonID, dayID, skierID);
            System.out.println("Success!");
            System.out.println(response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling SkiersApi#writeNewLiftRide");
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void runProject1Part1() {
        P1Part1.main(null);
    }
}
