package cycling;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;


/**
 * ResultsClass. A class that keeps track of Riders results based on their riderId
 *
 * @author Rhys Broughton, Sam Tebbet
 * @version 2.0
 */

public class ResultsClass implements Serializable {
    private final int riderId;

    private HashMap<Integer, LocalTime[]> checkpointMap;
    private HashMap<Integer, Integer> stagePointsMap;
    private HashMap<Integer, LocalTime> adjustedTime;
    private HashMap<Integer, Integer> mountainPointsMap;

    /**
     * Constructor just uses a riderId to initialise the class
     * @param riderId the riders Id
     */
    ResultsClass(int riderId) {
        this.riderId = riderId;
        this.checkpointMap = new HashMap<Integer, LocalTime[]>();
        this.adjustedTime = new HashMap<Integer, LocalTime>();
        this.stagePointsMap = new HashMap<Integer, Integer>();
        this.mountainPointsMap = new HashMap<Integer, Integer>();
    }

    /**
     * A method that returns the riderId for the results
     * @return the riderId
     */
    public int getRiderId() {
        return riderId;
    }

    /**
     * A method that returns the riders list of checkpoints in a specific stage
     * @param stageId the stageId with the checkpoint times
     * @return the list of checkpoints for the stage
     */
    public LocalTime[] getCheckpoints(int stageId) {
        return checkpointMap.get(stageId);
    }

    /**
     * A method that returns the elapsed time a rider took to complete a stage
     * @param stageId the stage Id to be looked at
     * @return the elapsed time for the stage
     */
    public long getElapsedTime(int stageId) {
        LocalTime finishTime = this.checkpointMap.get(stageId)[this.checkpointMap.get(stageId).length - 1];
        LocalTime startTime = this.checkpointMap.get(stageId)[0];
        return Duration.between(startTime, finishTime).toMillis();
    }

    /**
     * A method that returns the start time of the stage
     * @param stageId the stage to be looked at
     * @return the start time of the stage
     */
    public LocalTime getStartTime(int stageId) {
        return this.checkpointMap.get(stageId)[0];
    }

    /**
     * A method that adds a list of checkpoints to the results for a stage
     * @param stageId The stage for checkpoints to be added
     * @param checkpoints The list of checkpoints
     */
    public void addCheckpoints(int stageId, LocalTime... checkpoints) {
        this.checkpointMap.put(stageId, checkpoints);
    }

    /**
     * A method that gets the riders points in a stage
     * @param stageId the stageId for the points
     * @return the points in the stage
     */
    public int getPointsFromStage(int stageId) {
        return this.stagePointsMap.get(stageId);
    }

    /**
     * A method that returns a HashMap of the stageIds -> points
     * @return the points for the stage
     */
    public HashMap<Integer, Integer> getStagePointsMap(){
        return stagePointsMap;
    }

    /**
     * A method that returns a HashMap of the stageIds -> Mountain Points
     * @return the mountain points hashmap
     */
    public HashMap<Integer, Integer> getMountainPointsMap(){
        return mountainPointsMap;
    }

    /**
     * A method that returns the riders mountain points in a stage
     * @param stageId the stage Id for the mountainPoints
     * @return the mountain points for the stage
     */
    public int getMountainPointsFromStage(int stageId) {
        return this.mountainPointsMap.get(stageId);
    }

    /**
     * Clears the Mountain Points from the ResultsClass
     */
    public void clearMountainPoints(){
        this.mountainPointsMap.clear();
    }

    /**
     * A method that adds mountain points to the HashMap based on stageId -> Mountain Points
     * @param stageId The stage Id for data to be put into
     * @param points The mountain points to be added
     */
    public void addMountainPointsToStage(int stageId, int points) {
        this.mountainPointsMap.put(stageId, points);
    }

    /**
     * A method to add points to the HashMap based on stageId -> Points
     * @param stageId The stage Id for data to be put into
     * @param points The points to be added
     */
    public void addPointsToStage(int stageId, int points) {
        this.stagePointsMap.put(stageId, points);
    }

    /**
     * A method that removes checkpoints from the HashMap based on the stageId -> Checkpoints
     * @param stageId the stageId for the checkpoints to be removed (Removes the stageId from the map as well)
     */
    public void removeCheckpoint(int stageId) {
        this.checkpointMap.remove(stageId);
    }

    /**
     * A method that adds the Adjusted time to a HashMap based on the stageId -> Adjusted time
     * @param stageId The stage Id for the adjusted time
     * @param adjustedTime The adjusted time
     */
    public void addAdjustedTime(int stageId, LocalTime adjustedTime) {
        this.adjustedTime.put(stageId, adjustedTime);
    }

    /**
     * A method that returns the Adjusted Time for a stage
     * @param stageId The stage Id to be looked at
     * @return The Adjusted time for the stage
     */
    public LocalTime getAdjustedTime(int stageId) {
        return this.adjustedTime.get(stageId);
    }

    /**
     * A method that clears all the Points for the rider
     * @param stageId
     */
    public void clearPoints(int stageId) {
        this.stagePointsMap.clear();
        this.mountainPointsMap.clear();
        this.adjustedTime.clear();
    }
}
