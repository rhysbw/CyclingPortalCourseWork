import cycling.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortalInterface interface -- note you
 * will want to increase these checks, and run it on your CyclingPortal class
 * (not the BadCyclingPortal class).
 *
 * @author Diogo Pacheco
 * @version 1.0
 */
public class CyclingPortalInterfaceTestApp {

    /**
     * Test method.
     *
     * @param args not used
     */


    public static void main(String[] args) throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, NameNotRecognisedException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointsException, IOException, ClassNotFoundException {
        System.out.println("The system compiled and started the execution...");

        MiniCyclingPortalInterface portal = new BadMiniCyclingPortal();
        CyclingPortal c = new CyclingPortal();
//		CyclingPortalInterface portal = new BadCyclingPortal()



        c.createRace("race1", "The first Race");
        c.createRace("race2", null);
        c.createRace("Race3", "The 3rd Race");
        c.addStageToRace(1, "Stage 1", "The first Stage", 10.2, LocalDateTime.now(), StageType.FLAT);
        c.addStageToRace(2, "Stage 2", "The 1 Stage", 10.2, LocalDateTime.now().plusHours(1), StageType.FLAT);

        c.addStageToRace(3, "Stage 3", "The 3 Stage", 10.2, LocalDateTime.now().plusSeconds(500), StageType.FLAT);// test


        c.addIntermediateSprintToStage(2, 1.0);
        c.addCategorizedClimbToStage(2, 5.0, SegmentType.C1, 0.3, 3.0);


        c.concludeStagePreparation(2);


        c.createTeam("team1", "theFirstTeam");
        c.createTeam("team2", "the2ndTeam");
        c.createTeam("team3", "the3rdTeam");
        c.createRider(1, "Paul", 2002);
        c.createRider(2, "Jeff", 2000);
        c.createRider(3, "John", 2012);
        c.createRider(3, "James", 2210);
        c.createRider(3, "Josh", 2080);

        LocalTime[] checkPoints = {LocalTime.parse("08:00"), LocalTime.parse("09:10"), LocalTime.parse("10:20"), LocalTime.parse("11:41:12.2")};
        LocalTime[] checkPoint2 = {LocalTime.parse("08:00"), LocalTime.parse("09:00"), LocalTime.parse("10:10"), LocalTime.parse("11:43:10.9")};
        LocalTime[] checkPoint3 = {LocalTime.parse("08:00"), LocalTime.parse("10:10"), LocalTime.parse("10:30"), LocalTime.parse("11:42:11.4")};
        LocalTime[] checkPoint4 = {LocalTime.parse("08:00"), LocalTime.parse("11:10"), LocalTime.parse("10:40"), LocalTime.parse("11:44:11.0")};
        LocalTime[] checkPoint5 = {LocalTime.parse("08:00"), LocalTime.parse("12:10"), LocalTime.parse("10:50"), LocalTime.parse("11:45:12.4")};
        c.registerRiderResultsInStage(2, 1, checkPoints);
        c.registerRiderResultsInStage(2, 2, checkPoint2);
        c.registerRiderResultsInStage(2, 3, checkPoint3);
        c.registerRiderResultsInStage(2, 4, checkPoint4);
        c.registerRiderResultsInStage(2, 5, checkPoint5);

        System.out.println(c.getRiderAdjustedElapsedTimeInStage(1,1));

    }
}
