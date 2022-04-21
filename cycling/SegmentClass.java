package cycling;

import java.io.Serializable;

/**
 *Segment class is a class for segments, this has 2 constructors for the intermediate sprint and one for mountain segments
 * @version 2.0
 * @author sam tebbet, rhys broughton
 */
public class SegmentClass implements Serializable {
    private static int totalNumberofSegments;
    private final int segmentId;
    private final Double location;
    private final SegmentType type;
    private final double length;
    private final double averageGradient;

    /**
     * This is the constructor for different types of climbs
     *
     * @param location The kilometre location where the climb finishes within the stage
     * @param type The type of segment in the ENUM SegmentType
     * @param averageGradient The average gradient of the climb
     * @param length The kilometre length of the segment
     */
    SegmentClass(double location, SegmentType type, double averageGradient, double length) {
        this.segmentId = ++totalNumberofSegments;
        this.location = location;
        this.type = type;
        this.averageGradient = averageGradient;
        this.length = length;
    }

    /**
     * This is the constructor for the sprint segments
     *
     * @param location
     */
    SegmentClass(double location){
        this.segmentId = ++totalNumberofSegments;
        this.location = location;
        this.type = SegmentType.SPRINT;
        this.averageGradient = 0;
        this.length = 1.0;
    }

    /**
     * A method that gets the average gradient of the Segment
     *
     * @return the average gradient
     */
    public double getAverageGradient() {
        return averageGradient;
    }

    /**
     * A method that gets the length
     *
     * @return the length of the segment
     */
    public double getLength() {
        return length;
    }

    /**
     * A method that gets the SegmentType of the segment
     *
     * @return the type of segment
     */
    public SegmentType getType() {
        return type;
    }

    /**
     * A method that gets the location
     *
     * @return the kilometre location of the end of the segment
     */
    public Double getLocation() {
        return location;
    }

    /**
     * A method that gets the segment ID
     *
     * @return the segment ID
     */
    public int getSegmentId() {
        return segmentId;
    }

}
