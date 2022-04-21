package cycling;

import java.io.Serializable;

/**
 * A class top hold all the data related to the rider and functions
 *
 * @author Sam Tebbet, Rhys Broughton
 * @version 2.0
 */
public class RiderClass implements Serializable {
    private static int totalNumberofRiders;
    private final int riderId;
    private final String name;
    private final int yearOfBirth;


    /**
     * Constructor for RiderClass
     * @param name the name of the rider
     * @param yearOfBirth the year of birth of the rider
     */
    RiderClass(String name, int yearOfBirth) {
        this.riderId = ++totalNumberofRiders;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
    }

    /**
     * A method that gets the rider id
     * @return the riderId
     */
    public int getRiderId() {
        return riderId;
    }

    /**
     * A method that gets the rider name
     * @return the name of the rider
     */
    public String getName() {
        return name;
    }

    /**
     * A method to get the year of birth of the rider
     * @return yearOfBirth of the rider
     */
    public int getYearOfBirth() {
        return yearOfBirth;
    }
}
