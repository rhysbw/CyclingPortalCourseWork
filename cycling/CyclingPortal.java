package cycling;

import java.io.*;
import java.lang.reflect.Array;
import java.time.*;
import java.util.*;

import static java.util.Collections.*;

/**
 * This is the Cycling Portal
 *
 * @author Sam Tebbet, Rhys Broughton
 * @version 1.0
 */

public class CyclingPortal implements CyclingPortalInterface {
    protected ArrayList<RaceClass> raceArray = new ArrayList<>();
    protected ArrayList<TeamClass> teamArray = new ArrayList<>();
    protected ArrayList<ResultsClass> resultsArray = new ArrayList<>();

    public ArrayList<RaceClass> getRaceArray() {
        return raceArray;
    }

    public ArrayList<TeamClass> getTeamArray() {
        return teamArray;
    }

    public ArrayList<ResultsClass> getResultsArray() {
        return resultsArray;
    }

    @Override
    public void removeRaceByName(String name) throws NameNotRecognisedException {
        try {
            raceArray.removeIf(i -> i.getRaceName().equals(name));
        } catch (NullPointerException e) {
            throw new NameNotRecognisedException();
        }
    }


    @Override
    public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }
        if (checkRegisteredResultsForRace(raceId)) {
            return new int[0];
        }

        // gets the total elapsed time for each of the stages and adds them up, then sorts by who has the least
        // ensure allFremove Adjusted Elapsed times for every stage in the race for every rider
        ArrayList<LocalTime> adjustedTimesForRace = new ArrayList<>();
        for (ResultsClass r : resultsArray) {
            LocalTime currentRiderTime = LocalTime.parse("00:00"); // times for each rider
            for (RaceClass i : raceArray) {
                if (i.getRaceId() == raceId) {
                    for (StageClass j : i.getRaceStages()) { // runs for however many stages in the race there are
                        getRiderAdjustedElapsedTimeInStage(j.getStageId(), r.getRiderId());
                        if (r.getAdjustedTime(j.getStageId()) != null) {
                            currentRiderTime = currentRiderTime
                                    .plusHours(r.getAdjustedTime(j.getStageId()).getHour())
                                    .plusMinutes(r.getAdjustedTime(j.getStageId()).getMinute())
                                    .plusSeconds(r.getAdjustedTime(j.getStageId()).getSecond())
                                    .plusNanos(r.getAdjustedTime(j.getStageId()).getNano()); // adds the new time to the current time
                        }
                    }
                    adjustedTimesForRace.add(currentRiderTime);
                }
            }
        }

        sort(adjustedTimesForRace); // now have a sorted list of all the Elapsed Adjusted Times for the whole race

        // fill array with the riderIds
        ArrayList<Integer> riderIdsArray = new ArrayList<>();
        for (LocalTime l : adjustedTimesForRace) {
            for (ResultsClass r : resultsArray) {
                LocalTime currentRiderTime = LocalTime.parse("00:00"); // times for each rider
                for (RaceClass i : raceArray) {
                    if (i.getRaceId() == raceId) { // ensures for correct race
                        for (StageClass j : i.getRaceStages()) { // runs for however many stages in the race there are
                            getRiderAdjustedElapsedTimeInStage(j.getStageId(), r.getRiderId());
                            if (r.getAdjustedTime(j.getStageId()) != null) {
                                currentRiderTime = currentRiderTime
                                        .plusHours(r.getAdjustedTime(j.getStageId()).getHour())
                                        .plusMinutes(r.getAdjustedTime(j.getStageId()).getMinute())
                                        .plusSeconds(r.getAdjustedTime(j.getStageId()).getSecond())
                                        .plusNanos(r.getAdjustedTime(j.getStageId()).getNano()); // adds the new time to the current time
                            }
                        }
                        if (currentRiderTime.equals(l)) {
                            // need to check for identical times by checking if the rider id has already been used
                            riderIdsArray.add(r.getRiderId());
                        }
                    }
                }
            }
        }

        return riderIdsArray.stream().mapToInt(Integer -> Integer).toArray(); // returns and converts ArrayList to int[]
    }

    @Override
    public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }
        if (checkRegisteredResultsForRace(raceId)) {
            return new LocalTime[0];
        }

        ArrayList<LocalTime> orderedTotalRaceElapsedTime = new ArrayList<>();

        for (Integer I : getRidersGeneralClassificationRank(raceId)) { // for each rider ID
            for (ResultsClass r : resultsArray) {
                if (r.getRiderId() == I) { // checks its the correct rider
                    LocalTime currentRiderTime = LocalTime.parse("00:00"); // times for each rider
                    for (RaceClass i : raceArray) {
                        if (i.getRaceId() == raceId) { // runs once per rider
                            for (StageClass j : i.getRaceStages()) {
                                LocalTime timeInLocalTime = Instant.ofEpochMilli(r.getElapsedTime(j.getStageId())).atZone(ZoneId.systemDefault()).toLocalTime();
                                currentRiderTime = currentRiderTime
                                        .plusHours(timeInLocalTime.getHour())
                                        .plusMinutes(timeInLocalTime.getMinute())
                                        .plusSeconds(timeInLocalTime.getSecond())
                                        .plusNanos(timeInLocalTime.getNano()); // adds to get the elapsed time for all stages for one rider
                            }
                            orderedTotalRaceElapsedTime.add(currentRiderTime); // adds to array in correct order
                        }
                    }
                }

            }
        }
        return orderedTotalRaceElapsedTime.toArray(new LocalTime[0]); // returns and converts ArrayList to LocalTime[]
    }

    @Override
    public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }
        if (checkRegisteredResultsForRace(raceId)) {
            return new int[0];
        }

        ArrayList<Integer> listOfStages = new ArrayList<>(); //List of stageIds in the race
        ArrayList<Integer> listOfRidersTotalPoints = new ArrayList<>();

        for (RaceClass i : raceArray) {
            if (i.getRaceId() == raceId) {
                for (StageClass j : i.getRaceStages()) {
                    listOfStages.add(j.getStageId()); // a list of all the stageIds in the race
                }
            }
        }
        int index = 0;
        for (ResultsClass r : resultsArray) {
            listOfRidersTotalPoints.add(0);
            for (int i : listOfStages) {
                getRidersPointsInStage(i);
                listOfRidersTotalPoints.set(index, listOfRidersTotalPoints.get(index) + r.getPointsFromStage(i));
            }
            index++;
        }

        return listOfRidersTotalPoints.stream().mapToInt(Integer -> Integer).toArray(); // returns and converts ArrayList to int[]
    }

    @Override
    public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }
        if (checkRegisteredResultsForRace(raceId)) {
            return new int[0];
        }

        ArrayList<Integer> listOfRidersTotalMountainPoints = new ArrayList<>();
        ArrayList<Integer> sortedList = new ArrayList<>();

        for (Integer I : getRidersGeneralClassificationRank(raceId)) { // for each rider ID
            for (ResultsClass r : resultsArray) {
                if (r.getRiderId() == I) { // checks its the correct rider
                    System.out.println(I);
                    int points = 0; // times for each rider
                    for (RaceClass i : raceArray) {
                        if (i.getRaceId() == raceId) { // runs once per rider
                            r.clearMountainPoints();
                            for (StageClass j : i.getRaceStages()) {

                                if (!r.getMountainPointsMap().containsKey(j.getStageId())) { // checks to see if there are mountain points for that rider for that stage
                                    getRidersMountainPointsInStage(j.getStageId());
                                }

                                System.out.println("Get Mountin point for stage: " + j.getStageId() + " Points:" + r.getMountainPointsFromStage(j.getStageId()));
                                System.out.println("Rider id" + r.getRiderId());
                                points = points + r.getMountainPointsFromStage(j.getStageId()); // adds the saved points to the total

                            }
                            listOfRidersTotalMountainPoints.add(points); // Add points to ArrayList in correct order
                        }
                    }
                }

            }
        }

        return listOfRidersTotalMountainPoints.stream().mapToInt(Integer -> Integer).toArray(); // converts from ArrayList to int[] and returns

    }

    @Override
    public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }
        if (checkRegisteredResultsForRace(raceId)) {
            return new int[0];
        }

        ArrayList<Integer> riderIds = new ArrayList<>();

        List<Integer> points = Arrays.stream(getRidersPointsInRace(raceId)).boxed().toList();//Converting Array to ArrayList for mutability
        ArrayList<Integer> sortedPoints = new ArrayList<>(points);

        sortedPoints.sort(reverseOrder()); //Gets the points in desc order

        for (int l : sortedPoints) {
            for (ResultsClass i : resultsArray) {
                int pointSum = 0;
                for (RaceClass j : raceArray) {
                    if (j.getRaceId() == raceId) {
                        for (StageClass k : j.getRaceStages()) {
                            pointSum += i.getPointsFromStage(k.getStageId()); // adds up all the points for each stage
                        }
                        if (pointSum == l) {
                            riderIds.add(i.getRiderId()); // adds the riderId in the correct order
                        }
                    }
                }

            }
        }

        return riderIds.stream().mapToInt(Integer -> Integer).toArray();
    }

    @Override
    public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }

        // check if all reselts have been registered for the raceId
        if (checkRegisteredResultsForRace(raceId)) {
            return new int[0];
        }


        ArrayList<Integer> riderIds = new ArrayList<>();
        HashMap<Integer, Integer> pointstoIdsMap = new HashMap<>();
        //Converting Array to ArrayList for mutability
        List<Integer> points = Arrays.stream(getRidersMountainPointsInRace(raceId)).boxed().toList();
        ArrayList<Integer> sortedPoints = new ArrayList<>(points);

        sortedPoints.sort(Collections.reverseOrder()); //Gets the points in desc order
        System.out.println(sortedPoints);

        for (ResultsClass i : resultsArray) {
            int pointSum = 0;
            for (RaceClass j : raceArray) {
                if (j.getRaceId() == raceId) {
                    for (StageClass k : j.getRaceStages()) {
                        pointSum += i.getMountainPointsFromStage(k.getStageId());
                    }
                    pointstoIdsMap.put(i.getRiderId(), pointSum);
                }
            }
        }
        for (int i : sortedPoints) {
            for (ResultsClass r : resultsArray) {
                if (i == pointstoIdsMap.get(r.getRiderId())) {
                    riderIds.add(r.getRiderId());
                }
            }
        }
        System.out.println(pointstoIdsMap);

        return riderIds.stream().mapToInt(Integer -> Integer).toArray();
    }

    @Override
    public int[] getRaceIds() {
        // returns an array of raceIds in the system
        ArrayList<Integer> raceIds = new ArrayList<>();
        for (RaceClass i : raceArray) {
            raceIds.add(i.getRaceId()); // adds the raceId to the array
        }
        return raceIds.stream().mapToInt(Integer -> Integer).toArray(); // coverts to simple int[]
    }

    @Override
    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
        // creates a race and adds to the raceArray

        //Exceptions
        if (checkIllegalRaceName(name)) {
            throw new IllegalNameException("Illegal Race Name: " + name);
        } else if (checkInvalidName(name)) {
            throw new InvalidNameException("Invalid Race Name: " + name);
        }

        if (checkIllegalRaceName(name)) {
            throw new IllegalNameException();
        }

        if (checkInvalidName(name)) {
            throw new InvalidNameException();
        }

        RaceClass newRace = new RaceClass(name, description);
        raceArray.add(newRace); // adds the new race object to the ArrayList
        return newRace.getRaceId(); // returns the raceId


    }

    @Override
    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {

        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }

        String raceDetailsString = ""; // empty string
        for (RaceClass i : raceArray) {
            if (i.getRaceId() == raceId) {
                raceDetailsString = i.getRaceId() + "\n" + i.getRaceName() + "\n" + i.getRaceDescription() + "\n" + Arrays.toString(getRaceStages(i.getRaceId()));
            }
        }
        return raceDetailsString;
    }

    @Override
    public void removeRaceById(int raceId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }

        raceArray.removeIf(i -> i.getRaceId() == raceId);

    }

    @Override
    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {

        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }
        for (RaceClass i : raceArray) {
            if (i.getRaceId() == raceId) { // finds the correct race by checking raceIds
                return getRaceStages(i.getRaceId()).length;
            }
        }
        return 0;
    }

    @Override
    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime
            startTime, StageType type)
            throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {

        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        } else if (checkIllegalStageName(stageName)) {
            throw new IllegalNameException("Illegal Stage Name: " + stageName);
        } else if (checkInvalidName(stageName)) {
            throw new InvalidNameException("Invalid Stage Name: " + stageName);
        } else if (checkLength(length)) {
            throw new InvalidLengthException("Length of stage is less than 5km: " + length);
        }

        StageClass newStage = new StageClass(stageName, description, length, startTime, type);
        for (RaceClass i : raceArray) {
            if (i.getRaceId() == raceId) { // checks is correct raceId to add to
                i.addStageToRace(newStage);
            }
        }
        return newStage.getStageId(); // returns the stageId
    }

    @Override
    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {

        //Exceptions
        if (checkRaceId(raceId)) {
            throw new IDNotRecognisedException("Race ID not recognised: " + raceId);
        }

        ArrayList<LocalDateTime> orderedTimes = new ArrayList<>();
        ArrayList<Integer> orderedStages = new ArrayList<>();

        for (RaceClass i : raceArray) {
            if (i.getRaceId() == raceId) {
                for (StageClass j : i.getRaceStages()) {
                    orderedTimes.add(j.getStartTime()); // add the start time to the ArrayList of times
                }
                sort(orderedTimes);
                for (LocalDateTime j : orderedTimes) { // each of the ordered times
                    for (StageClass k : i.getRaceStages()) { // for each of the stages
                        if (j == k.getStartTime()) { // if the startTime of the object is the same as the ordered list proceed
                            orderedStages.add(k.getStageId()); // adds the stageID to the orderedStages ArrayList
                        }
                    }
                }
            }
        }
        return orderedStages.stream().mapToInt(i -> i).toArray(); // Converting the orderedStages ArrayList to an Array
    }

    @Override
    public double getStageLength(int stageId) throws IDNotRecognisedException {
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        }
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    return j.getLength();
                }
            }
        }
        return 0;
    }

    @Override
    public void removeStageById(int stageId) throws IDNotRecognisedException {
        try {
            for (RaceClass i : raceArray) {
                i.getRaceStages().removeIf(j -> j.getStageId() == stageId);
            }
        } catch (NullPointerException e) {
            throw new IDNotRecognisedException();
        }
    }

    @Override
    public int addCategorizedClimbToStage(int stageId, Double location, SegmentType type, Double
            averageGradient, Double length)
            throws
            IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {

        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        } else if (checkLocation(stageId, location)) {
            throw new InvalidLocationException("Location is out of bounds of the stage length: " + location);
        } else if (checkStageState(stageId)) {
            throw new InvalidStageStateException("Stage is \"waiting for results\"");
        } else if (checkStageType(stageId)) {
            throw new InvalidStageTypeException("Time-trial stages cannot contain any segment");
        }

        // Creates a new segments
        SegmentClass newSegment = new SegmentClass(location, type, averageGradient, length);
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    j.addSegmentToStage(newSegment);
                }
            }
        }
        return newSegment.getSegmentId();
    }

    @Override
    public int addIntermediateSprintToStage(int stageId, double location)
            throws
            IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {

        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        } else if (checkLocation(stageId, location)) {
            throw new InvalidLocationException("Location is out of bounds of the stage length: " + location);
        } else if (checkStageState(stageId)) {
            throw new InvalidStageTypeException("Time-trial stages cannot contain any segment");
        }

        SegmentClass newSegment = new SegmentClass(location);
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    j.addSegmentToStage(newSegment); // adds the newSegment object to the Arrays
                }
            }
        }
        return newSegment.getSegmentId();
    }

    @Override
    public void removeSegment(int segmentId) throws IDNotRecognisedException, InvalidStageStateException {
        // removes a segment
        //Exceptions
        if (checkSegmnentId(segmentId)) {
            throw new IDNotRecognisedException("Segment ID not recognised: " + segmentId);
        } else if (checkStageStateFromSegment(segmentId)) {
            throw new InvalidStageStateException("Stage is \"waiting for results\"");
        }

        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                j.getStageSegments().removeIf(k -> k.getSegmentId() == segmentId); // removes the segment with the corresponding segmentId
            }
        }
    }

    @Override
    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {

        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        } else if (checkStageState(stageId)) {
            throw new InvalidStageStateException("Stage is \"waiting for results\"");
        }

        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    j.setStageState("running");
                    break;
                }
            }
        }
    }

    @Override
    public int[] getStageSegments(int stageId) throws IDNotRecognisedException {

        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        }

        ArrayList<Double> stageSegments = new ArrayList<>();
        ArrayList<Integer> sortedSegments = new ArrayList<>();
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    for (SegmentClass k : j.getStageSegments()) {
                        stageSegments.add(k.getLength()); // gets the length of each segment
                    }
                    sort(stageSegments); // sorts by the length
                    for (Double l : stageSegments) {
                        for (SegmentClass k : j.getStageSegments()) {
                            if (l == k.getLength()) {
                                sortedSegments.add(k.getSegmentId()); // adds the segmentId to the array in the correct order
                            }
                        }
                    }
                }
            }
        }
        return sortedSegments.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {

        //Exceptions
        if (checkIllegalTeamName(name)) {
            throw new IllegalNameException("The Team name already exists on the platform: " + name);
        } else if (checkInvalidName(name)) {
            throw new InvalidNameException("This name is invalid: " + name);
        }

        TeamClass newTeam = new TeamClass(name, description);
        teamArray.add(newTeam);
        return newTeam.getTeamId();
    }

    @Override
    public void removeTeam(int teamId) throws IDNotRecognisedException {
        //Exceptions
        if (checkTeamId(teamId)) {
            throw new IDNotRecognisedException("Team ID not recognised: " + teamId);
        }
        teamArray.removeIf(i -> i.getTeamId() == teamId);
    }

    @Override
    public int[] getTeams() {
        ArrayList<Integer> teamIds = new ArrayList<>();
        for (TeamClass i : teamArray) {
            teamIds.add(i.getTeamId());
        }

        return teamIds.stream().mapToInt(Integer -> Integer).toArray();
    }

    @Override
    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {

        //Exceptions
        if (checkTeamId(teamId)) {
            throw new IDNotRecognisedException("Team ID not recognised: " + teamId);
        }

        ArrayList<Integer> riderIds = new ArrayList<>();
        for (TeamClass i : teamArray) {
            if (i.getTeamId() == teamId) {
                for (RiderClass j : i.getTeamRiders()) {
                    riderIds.add(j.getRiderId());
                }
            }
        }

        return riderIds.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public int createRider(int teamId, String name, int yearOfBirth) throws
            IDNotRecognisedException, IllegalArgumentException {

        //Exceptions
        if (checkTeamId(teamId)) {
            throw new IDNotRecognisedException("Team ID not recognised: " + teamId);
        } else if (checkIllegalArgument(name, yearOfBirth)) {
            throw new IllegalArgumentException("The rider name or YoB is illegal: " + name + " : " + yearOfBirth);
        }
        RiderClass newRider = new RiderClass(name, yearOfBirth);
        for (TeamClass i : teamArray) {
            if (i.getTeamId() == teamId) {
                i.addRiderToTeam(newRider); // adds the rider to the correct team via teamId

            }
        }
        resultsArray.add(new ResultsClass(newRider.getRiderId())); // creates a new object in the resultsArray for the rider
        return newRider.getRiderId();
    }

    @Override
    public void removeRider(int riderId) throws IDNotRecognisedException {
        //Exceptions
        if (checkRiderId(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised: " + riderId);
        }
        for (TeamClass i : teamArray) {
            i.getTeamRiders().removeIf(j -> j.getRiderId() == riderId);
        }
    }

    @Override
    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
            throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointsException,
            InvalidStageStateException {
        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        } else if (checkRiderId(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised: " + riderId);
        } else if (checkCheckpoints(stageId, checkpoints)) {
            throw new InvalidCheckpointsException("Invalid checkpoints");
        } else if (checkStageState(stageId)) {
            throw new InvalidStageStateException("Stage state is \"waiting for results\"");
        } else if (checkDuplicatedResults(stageId, riderId)) {
            throw new DuplicatedResultException("This rider already has a result in this stage");
        }


        for (ResultsClass i : resultsArray) {
            if (i.getRiderId() == riderId) {
                i.addCheckpoints(stageId, checkpoints);
            }
        }
    }

    @Override
    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        } else if (checkRiderId(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised: " + riderId);
        }

        for (ResultsClass i : resultsArray) {
            if (i.getRiderId() == riderId) {
                if (i.getCheckpoints(stageId) != null){ // if there have been results registered for this stage and rider
                    return i.getCheckpoints(stageId);
                }
            }
        }

        return new LocalTime[0];

    }

    @Override
    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {

        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        } else if (checkRiderId(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised: " + riderId);
        }

        // checsk there are results for the stage
        if (checkRegisteredResultsForStage(stageId)){
            return null;
        }


        //Checking that the race is not a time trial
        //-------------------------------------------------------------------------------------------------------------
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (stageId == j.getStageId() && j.getType() == StageType.TT) {
                    for (ResultsClass k : resultsArray) {
                        if (riderId == k.getRiderId()) {
                            k.addAdjustedTime(stageId, Instant.ofEpochMilli(k.getElapsedTime(stageId)).atZone(ZoneId.systemDefault()).toLocalTime());
                            return k.getAdjustedTime(stageId);
                        }
                    }
                }
            }
        }
        //-------------------------------------------------------------------------------------------------------------
        HashMap<Integer, LocalTime> elapsedTimeMap = new HashMap<>();
        ArrayList<LocalTime> allRidersElapsedTime = new ArrayList<>();
        for (ResultsClass i : resultsArray) {
            allRidersElapsedTime.add(Instant.ofEpochMilli(i.getElapsedTime(stageId)).atZone(ZoneId.systemDefault()).toLocalTime());
        }
        sort(allRidersElapsedTime);

        //Checking to see which position in the array is the LocalTime for the riderId
        //-------------------------------------------------------------------------------------------------------------
        int riderIndex = 0;
        for (ResultsClass r : resultsArray) {
            if (r.getRiderId() == riderId) {

                for (LocalTime i : allRidersElapsedTime) {
                    if (Instant.ofEpochMilli(r.getElapsedTime(stageId)).atZone(ZoneId.systemDefault()).toLocalTime().equals(i)) {
                        break;
                    } else {
                        riderIndex++;
                    }
                }
                break;
            }
        }
        //System.out.println("rider index: " + riderIndex);
        //Adjusting the rider times
        //-------------------------------------------------------------------------------------------------------------
        ArrayList<LocalTime> adjustedTimes = new ArrayList<>();
        LocalTime temp = allRidersElapsedTime.get(0);

        for (LocalTime i : allRidersElapsedTime) {
            if (Duration.between(temp, i).toMillis() < 1000) { // checks if there was less than a second of time different between the times
                adjustedTimes.add(temp);
            } else {
                adjustedTimes.add(i);
            }
            temp = i;
        }

        for (ResultsClass r : resultsArray) {
            if (r.getRiderId() == riderId) {
                r.addAdjustedTime(stageId, adjustedTimes.get(riderIndex)); // saves teh adjusted times to the results class for future use
            }
        }

        return adjustedTimes.get(riderIndex);
    }

    @Override
    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        } else if (checkRiderId(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised: " + riderId);
        }

        for (ResultsClass i : resultsArray) {
            if (i.getRiderId() == riderId) {
                i.removeCheckpoint(stageId); // removes the checkpoints
                i.clearPoints(stageId); // removes the points
            }
        }

    }

    @Override
    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        }else if (checkRegisteredResultsForStage(stageId)){
            return new int[0];
        }


        ArrayList<Long> orderedTimes = new ArrayList<Long>();
        ArrayList<Integer> raceIds = new ArrayList<Integer>();
        for (ResultsClass i : resultsArray) {
            orderedTimes.add(i.getElapsedTime(stageId));
        }

        sort(orderedTimes);


        for (long j : orderedTimes) {
            for (ResultsClass i : resultsArray) {
                if (i.getElapsedTime(stageId) == j) {
                    raceIds.add(i.getRiderId()); // adds the raceIds to the array list in correct
                }
            }
        }
        return raceIds.stream().mapToInt(i -> i).toArray(); // converts ArrayList to int[]

    }

    @Override
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        }
        if (checkRegisteredResultsForStage(stageId)){
            return new LocalTime[0];
        }

        ArrayList<LocalTime> adjustedTimeArray = new ArrayList<>();
        ArrayList<LocalTime> finalTimeArray = new ArrayList<>();

        for (ResultsClass i : resultsArray) {
            adjustedTimeArray.add(getRiderAdjustedElapsedTimeInStage(stageId, i.getRiderId())); // adds the elapsed time for the rider to an array
        }
        sort(adjustedTimeArray);

        for (LocalTime i : adjustedTimeArray) {
            boolean done = false;
            for (Integer j : getRidersRankInStage(stageId)) {
                if (i.equals(getRiderAdjustedElapsedTimeInStage(stageId, j)) && !done) {
                    finalTimeArray.add(i); // adds the finalTime to the array in correct order
                    done = true;
                }
            }
        }

        return finalTimeArray.toArray(new LocalTime[0]); // converts from ArrayList to LocalTime[]
    }

    @Override
    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {

        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        }
        if (checkRegisteredResultsForStage(stageId)){
            return new int[0];
        }

        ArrayList<Integer> pointsArray = new ArrayList<>();
        // all the points that can be allocated
        int[] pointsFlat = {50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2};
        int[] pointsHillyFinish = {30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2};
        int[] pointsHighMountain = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] timeTrial = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] intermediateSprint = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};


        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    // only runs once
                    int pointsIndex = 0;

                    switch (j.getType()) {
                        case FLAT:
                            for (Integer l : getRidersRankInStage(stageId)) {
                                for (ResultsClass r : resultsArray) {
                                    if (l == r.getRiderId()) {
                                        if (pointsIndex <= (pointsFlat.length - 1)){ // checks if all the points have been allocated
                                            r.addPointsToStage(stageId, pointsFlat[pointsIndex]);
                                        }
                                        pointsIndex++;
                                    }
                                }
                            }
                            break;
                        case MEDIUM_MOUNTAIN:
                            for (Integer l : getRidersRankInStage(stageId)) {
                                for (ResultsClass r : resultsArray) {
                                    if (l == r.getRiderId()) {
                                        if (pointsIndex <= (pointsHillyFinish.length -1)){ // checks if all the points have been allocated
                                            r.addPointsToStage(stageId, pointsHillyFinish[pointsIndex]);
                                        }
                                        pointsIndex++;
                                    }
                                }
                            }
                            break;
                        case HIGH_MOUNTAIN:
                            for (Integer l : getRidersRankInStage(stageId)) {
                                for (ResultsClass r : resultsArray) {
                                    if (l == r.getRiderId()) {
                                        if (pointsIndex <= (pointsHighMountain.length -1)){ // checks if all the points have been allocated
                                            r.addPointsToStage(stageId, pointsHighMountain[pointsIndex]);
                                        }
                                        pointsIndex++;
                                    }
                                }
                            }
                            break;
                        case TT:
                            for (Integer l : getRidersRankInStage(stageId)) {
                                for (ResultsClass r : resultsArray) {
                                    if (l == r.getRiderId()) {
                                        if (pointsIndex <= (timeTrial.length-1)){ // checks if all the points have been allocated
                                            r.addPointsToStage(stageId, timeTrial[pointsIndex]);
                                        }
                                        pointsIndex++;
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }

        ArrayList<LocalTime> sortedTimes = new ArrayList<>(); //array of the start
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) { // only runs once
                    ArrayList<Double> sortedLocations = new ArrayList<>(); //array of locations of segments
                    for (SegmentClass k : j.getStageSegments()) {
                        sortedLocations.add(k.getLocation()); //gets the location for each segment in the stage
                    }
                    sort(sortedLocations); //sorts
                    int locationsIndex = 1;
                    for (SegmentClass k : j.getStageSegments()) {
                        int sprintIndex = 0;
                        for (Double d : sortedLocations) {
                            if (k.getType() == SegmentType.SPRINT) { // checks if it is a sprint segment
                                if (d.equals(k.getLocation())) {
                                    try {
                                        for (ResultsClass r : resultsArray) {
                                            sortedTimes.add(r.getCheckpoints(stageId)[locationsIndex]); // time finsihed sprint segment
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        throw new IndexOutOfBoundsException(e + ": Checkpoint Time not Found");
                                    }
                                    sort(sortedTimes);

                                    for (LocalTime l : sortedTimes) {
                                        for (ResultsClass r : resultsArray) {
                                            if (l == r.getCheckpoints(stageId)[locationsIndex]) {
                                                for (Integer I : getRidersRankInStage(stageId)) {
                                                    if (I == r.getRiderId()) {
                                                        int tempPoints = 0;
                                                        try {
                                                            tempPoints = r.getPointsFromStage(stageId); // gets the current stored points if there are any
                                                        } catch (NullPointerException ignored) {
                                                        }
                                                        try { // checks if all the points have been allocated
                                                            r.addPointsToStage(stageId, tempPoints + intermediateSprint[sprintIndex]);
                                                            sprintIndex++;
                                                        } catch (IndexOutOfBoundsException e) {
                                                            r.addPointsToStage(stageId, tempPoints);

                                                        }

                                                    }
                                                }

                                            }
                                        }
                                    }
                                    break;
                                }
                            }

                        }
                        locationsIndex++;
                    }
                }
            }
        }
        for (int i : getRidersRankInStage(stageId)) {
            for (ResultsClass r : resultsArray) {
                if (i == r.getRiderId()) {
                    pointsArray.add(r.getPointsFromStage(stageId)); // adds the points in the correct order to the array
                }
            }
        }

        return pointsArray.stream().mapToInt(i -> i).toArray(); // converts arrayList to int[]

    }

    @Override
    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {

        //Exceptions
        if (checkStageId(stageId)) {
            throw new IDNotRecognisedException("Stage ID not recognised: " + stageId);
        }
        if (checkRegisteredResultsForStage(stageId)){
            return new int[0];
        }

        ArrayList<Integer> points = new ArrayList<>();
        // all the points that can be allocated for different types of mountain types
        int[] c4 = {1};
        int[] c3 = {2, 1};
        int[] c2 = {5, 3, 2, 1};
        int[] c1 = {10, 8, 6, 4, 2, 1};
        int[] hc = {20, 15, 12, 10, 8, 6, 4, 2};
        ArrayList<LocalTime> sortedTimes = new ArrayList<>(); //array of the start times for each segment
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    // only runs once

                    ArrayList<Double> sortedLocations = new ArrayList<>();
                    for (SegmentClass k : j.getStageSegments()) {
                        sortedLocations.add(k.getLocation()); //gets the location for each segment in the stage
                    }
                    sort(sortedLocations); // sorts based on which order the segments are in based on their location in the stage
                    int locationsIndex = 1;


                    for (ResultsClass r : resultsArray) {
                        r.clearMountainPoints(); // resets the mountain points
                    }

                    for (SegmentClass k : j.getStageSegments()) {
                        int mountainIndex = 0;
                        for (Double d : sortedLocations) {

                            if (k.getType() != SegmentType.SPRINT) { // ensures there is a mountain segment

                                if (d.equals(k.getLocation())) {
                                    try {
                                        for (ResultsClass r : resultsArray) {
                                            sortedTimes.add(r.getCheckpoints(stageId)[locationsIndex]); // time finsihed the segment
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        throw new IndexOutOfBoundsException(e + ": Checkpoint Time not Found");

                                    }
                                    sort(sortedTimes);
                                    for (LocalTime l : sortedTimes) {
                                        for (ResultsClass r : resultsArray) {
                                            if (l == r.getCheckpoints(stageId)[locationsIndex]) {
                                                for (Integer I : getRidersRankInStage(stageId)) {
                                                    if (I == r.getRiderId()) {
                                                        int tempPoints = 0;
                                                        try {

                                                            tempPoints = r.getMountainPointsFromStage(stageId); // if there are points sorted for that stage get them
                                                        } catch (NullPointerException ignored) {
                                                        }
                                                        try {
                                                            switch (k.getType()) {
                                                                // adds the mountain points already gained in the stage to the ones for this segment
                                                                case C1 -> {
                                                                    r.addMountainPointsToStage(stageId, tempPoints + c1[mountainIndex]);
                                                                    mountainIndex++;
                                                                }
                                                                case C2 -> {
                                                                    r.addMountainPointsToStage(stageId, tempPoints + c2[mountainIndex]);
                                                                    mountainIndex++;
                                                                }
                                                                case C3 -> {
                                                                    r.addMountainPointsToStage(stageId, tempPoints + c3[mountainIndex]);
                                                                    mountainIndex++;
                                                                }
                                                                case C4 -> {
                                                                    r.addMountainPointsToStage(stageId, tempPoints + c4[mountainIndex]);
                                                                    mountainIndex++;
                                                                }
                                                                case HC -> {
                                                                    r.addMountainPointsToStage(stageId, tempPoints + hc[mountainIndex]);
                                                                    mountainIndex++;
                                                                }
                                                            }
                                                        } catch (IndexOutOfBoundsException e) {
                                                            //runs if all the points have been allocated
                                                            r.addMountainPointsToStage(stageId, tempPoints);
                                                        }


                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        locationsIndex++;

                    }
                }
            }
        }
        for (int i : getRidersRankInStage(stageId)) {
            for (ResultsClass r : resultsArray) {
                if (i == r.getRiderId()) {
                    try {
                        points.add(r.getMountainPointsFromStage(stageId)); // adds points to array in correct order
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }

        return points.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public void eraseCyclingPortal() {
        // empties all the arrayLists
        raceArray = new ArrayList<>();
        teamArray = new ArrayList<>();
        resultsArray = new ArrayList<>();
    }

    @Override
    public void saveCyclingPortal(String filename) throws IOException {
        // saves the data to the file
        try {
            System.out.println("Saving...\n");
            FileOutputStream fout = new FileOutputStream(filename);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(raceArray);
            System.out.println("Race Array Saved");
            oout.writeObject(teamArray);
            System.out.println("Team Array Saved");
            oout.writeObject(resultsArray);
            System.out.println("Results Array Saved");
            oout.close();
            fout.close();
            System.out.printf("Saving Completed to %s%n", filename);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("Serialization Failed");
        }
    }

    @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
        ArrayList<RaceClass> raceArrayTemp = null;
        ArrayList<TeamClass> teamArrayTemp = null;
        ArrayList<ResultsClass> resultsArrayTemp = null;


        try {
            System.out.println("Loading...");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename)); // connects to the file
            // saves the objects to arraylIsts
            raceArrayTemp = (ArrayList<RaceClass>) in.readObject();
            teamArrayTemp = (ArrayList<TeamClass>) in.readObject();
            resultsArrayTemp = (ArrayList<ResultsClass>) in.readObject();

            in.close();
            System.out.println("Loading Complete");
        } catch (IOException ex) {
            throw new IOException("Deserialization Failed");
        }
        // updates the main arrays with the loaded data
        raceArray = new ArrayList<>(raceArrayTemp);
        teamArray = new ArrayList<>(teamArrayTemp);
        resultsArray = new ArrayList<>(resultsArrayTemp);
    }

//-----------------------------------------------------------------------------------------------------------------
//BOOLEAN EXCEPTION CHECKERS

    private boolean checkStageId(int stageId) {
        // If the ID does not match to any stage in the system
        boolean contains = false;
        for (RaceClass i : raceArray) {
            for (StageClass k : i.getRaceStages()) {
                if (k.getStageId() == stageId) {
                    contains = true;
                    break;
                }
            }
        }
        return !contains;
    }

    private boolean checkRaceId(int raceId) {
        //If the ID does not match to any race in the system
        boolean contains = false;
        for (RaceClass i : raceArray) {
            if (i.getRaceId() == raceId) {
                contains = true;
                break;
            }
        }
        return !contains;
    }

    private boolean checkSegmnentId(int segmentId) {
        // checks to see fi there is a segment with that segment id
        boolean contains = false;
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                for (SegmentClass k : j.getStageSegments()) {
                    if (k.getSegmentId() == segmentId) {
                        contains = true;
                        break;
                    }
                }
            }
        }
        return !contains;
    }

    private boolean checkTeamId(int teamId) {
        // checks to see if there is a team with that teamId
        boolean contains = false;
        for (TeamClass i : teamArray) {
            if (i.getTeamId() == teamId) {
                contains = true;
                break;
            }
        }
        return !contains;
    }

    private boolean checkRiderId(int riderId) {
        // checks to see if there is a rider with that riderId
        boolean contains = false;
        for (TeamClass i : teamArray) {
            for (RiderClass j : i.getTeamRiders()) {
                if (j.getRiderId() == riderId) {
                    contains = true;
                    break;
                }
            }
        }
        return !contains;
    }

    private boolean checkStageState(int stageId) {
        // If the stage is "waiting for results" return true;
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    if (j.getStageState().equals("waiting for results")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkStageStateFromSegment(int segmentId) {
        // checks the stage of a segment
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                for (SegmentClass k : j.getStageSegments()) {
                    if (k.getSegmentId() == segmentId) {
                        if (j.getStageState().equals("waiting for results")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkStageType(int stageId) {
        //Time-trial stages cannot contain any segment
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    return j.getType() == StageType.TT;
                }
            }
        }
        return false;
    }

    private boolean checkLocation(int stageId, double location) {
        // If the location is out of bounds of the stage length.
        for (RaceClass i : raceArray) {
            for (StageClass k : i.getRaceStages()) {
                if (k.getStageId() == stageId) {
                    if (location > k.getLength()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkLength(double length) {
        //If the length is less than 5km.
        return length <= 5.0;
    }

    private boolean checkIllegalTeamName(String name) {
        // If the name already exists
        for (TeamClass i : teamArray) {
            if (i.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIllegalStageName(String name) {
        // If the name already exists
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIllegalRaceName(String name) {
        // If the name already exists
        for (RaceClass i : raceArray) {
            if (i.getRaceName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkInvalidName(String name) {
        // ensure there is a valid name
        if (name == null) {
            return true;
        } else if (name.length() > 30) {
            return true;
        } else return name.equals("");
    }

    private boolean checkIllegalArgument(String name, int YOB) {
        // ensures no illegal argument
        if (name == null) {
            return true;
        } else {
            return YOB < 1900;
        }
    }

    private boolean checkCheckpoints(int stageId, LocalTime... checkpoints) {
        // Ensures that there is a correct amount of times in the checkpoints Array
        for (RaceClass i : raceArray) {
            for (StageClass j : i.getRaceStages()) {
                if (j.getStageId() == stageId) {
                    if (j.getNumberOfSegments() >= (checkpoints.length + 2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkDuplicatedResults(int stageId, int riderId) {
        // checks to see if results have already been registered for the rider and stage
        for (ResultsClass i : resultsArray) {
            if (i.getRiderId() == riderId) {
                return i.getStagePointsMap().containsKey(stageId);
            }
        }
        return false;
    }

    private boolean checkRegisteredResultsForRace(int raceId) {
        for (ResultsClass r : resultsArray) {
            for (RaceClass i : raceArray) {
                if (i.getRaceId() == raceId) {
                    for (StageClass j : i.getRaceStages()) {
                        if (r.getCheckpoints(j.getStageId()) == null) {
                            return true; // true if no results
                        }
                    }
                }

            }
        }
        return false;
    }

    private boolean checkRegisteredResultsForStage(int stageId) {
        for (ResultsClass r : resultsArray) {
            for (RaceClass i : raceArray) {
                for (StageClass j : i.getRaceStages()) {
                    if (j.getStageId() == stageId) {
                        if (r.getCheckpoints(j.getStageId()) == null) {
                            return true; // true if no results
                        }
                    }
                }
            }
        }
        return false;
    }

}
