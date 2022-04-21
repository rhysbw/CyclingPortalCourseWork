package cycling;

import java.io.Serializable;
import java.sql.Array;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains one constructor for the Stages in order to generate a stage
 *
 * @version 1.0
 * @author Sam Tebbet, Rhys Broughton
 */
public class StageClass implements Serializable {
    private static int totalNumberofStages;
    private final int stageId;
    private final String stageName;
    private final String description;
    private final double length;
    private final LocalDateTime startTime;
    private final StageType type;
    protected ArrayList<SegmentClass> segmentsArray;
    private String stageState;

    /**
     * This constructs the data
     * @param stageName   the name of the stage
     * @param description a brief description of the stage
     * @param length      the length of the stage
     * @param startTime   the time of starting of the stage
     * @param type        the type of stage
     */
    StageClass(String stageName, String description, double length, LocalDateTime startTime, StageType type) {
        this.stageName = stageName;
        this.stageId = ++totalNumberofStages;
        this.description = description;
        this.startTime = startTime;
        this.type = type;
        this.stageState = "not waiting for results";
        this.length = length;
        this.segmentsArray = new ArrayList<SegmentClass>();

    }

    /**
     * This adds a segment to the array of segments for the relevant stage
     * @param segment the segment object to be added
     */
    public void addSegmentToStage(SegmentClass segment) {
        this.segmentsArray.add(segment);
    }

    /**
     * This gets all the segments of the stage
     * @return the array of all the segment data
     */
    public ArrayList<SegmentClass> getStageSegments() {
        return this.segmentsArray;
    }

    /**
     * This gets the Stage ID
     * @return the stageID
     */
    public int getStageId() {
        return stageId;
    }

    /**
     * A method that gets the stage name
     * @return the stages name
     */
    public String getStageName() {
        return stageName;
    }

    /**
     * A method that gets the description of the stage
     * @return the stage description
     */
    public String getDescription() {
        return description;
    }

    /**
     * A method that gets the start time of the stage
     * @return The start time of the stage
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * A method that gets the type of the stage
     * @return the stage type
     */
    public StageType getType() {
        return type;
    }

    /**
     * A method that gets the length of the stage
     * @return the length of the stage
     */
    public double getLength() {
        return length;
    }

    /**
     * A method the gets the stage state
     * @return the state of the stage
     */
    public String getStageState() {
        return stageState;
    }

    /**
     * A method that sets the state of the stage
     * @param state the state of the stage
     */
    public void setStageState(String state) {
        this.stageState = state;
    }

    /**
     * A method that gets the number of segments in the stage
     * @return the number of segments in the class
     */
    public int getNumberOfSegments(){
        return this.segmentsArray.size();
    }
}
