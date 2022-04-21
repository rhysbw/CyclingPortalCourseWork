package cycling;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A Class to hold all the related data to the teams
 *
 * @author Sam Tebbet, Rhys Broughton
 * @version 2.0
 */

public class TeamClass implements Serializable {
    private static int totalNumberofTeams;
    private final int teamId;
    private final String name;
    private final String description;
    private ArrayList<RiderClass> teamRiders;


    /**
     * The constructor for the TeamClass
     * @param name the name of the team
     * @param description the description of the team (can be null)
     */
    TeamClass(String name, String description){
        this.teamId = ++totalNumberofTeams;
        this.name = name;
        this.description = description;
        this.teamRiders = new ArrayList<RiderClass>();
    }

    /**
     * A method to get the teamId
     * @return the teamId
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * A method to get the team name
     * @return the team name
     */
    public String getName() {
        return name;
    }

    /**
     * A method to get the team description
     * @return the team description
     */
    public String getDescription() {
        return description;
    }

    /**
     * A method to get the riders of the team
     * @return the array of rider objects
     */
    public ArrayList<RiderClass> getTeamRiders() {
        return teamRiders;
    }

    /**
     * A method to add riders to the team
     * @param rider the rider to be added
     */
    public void addRiderToTeam(RiderClass rider){
        this.teamRiders.add(rider);
    }
}
