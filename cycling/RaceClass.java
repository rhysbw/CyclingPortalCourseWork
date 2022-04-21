package cycling;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * A class to hold all the data related to the race and functions
 *
 * @author Rhys Broughton, Sam Tebbet
 * @version 2.0
 */

public class RaceClass implements Serializable {
    private static int totalNumberofRaces;
    private final int raceId;
    private final String raceName;
    private final String raceDescription;
    private double length;
    private ArrayList<StageClass> stageArray;


    /**
     * Constructor for RaceClass
     * @param raceName the name of the Race
     * @param raceDescription the description of the race (can be null)
     */
    RaceClass(String raceName, String raceDescription) {
        this.raceId = ++totalNumberofRaces;
        this.raceName = raceName;
        this.raceDescription = raceDescription;
        this.stageArray = new ArrayList<>();

    }

    /**
     * Method that adds a stage to the race
     * @param stage The stage to be added
     */
    void addStageToRace(StageClass stage) {
        this.stageArray.add(stage);
    }

    /**
     * A method that returns the Id of the race
     * @return the race Id
     */
    public int getRaceId() {
        return this.raceId;
    }

    /**
     * A method that returns the name of the race
     * @return the name of the race
     */
    public String getRaceName() {
        return this.raceName;
    }

    /**
     * A method that returns the description of the race
     * @return the race description
     */
    public String getRaceDescription() {
        return this.raceDescription;
    }

    /**
     * A method that returns the stages in the race
     * @return the array of stages
     */
    public ArrayList<StageClass> getRaceStages() {
        return this.stageArray;
    }

}
