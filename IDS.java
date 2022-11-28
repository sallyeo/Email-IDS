/*
262 Assignment 3
Members:
Sally (4603229)
Goh Wee Shen (6747747)
*/


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class IDS
{
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String ANSI_YELLOW = "\u001B[33m";


    public static void main(String[] args) throws FileNotFoundException
    {
        EventType[] initialStats;   // to store elements read from Events.txt

        if(args.length!=3)
        {
            System.out.println("Command: IDS Events.txt Stats.txt Days");
            System.exit(-1);
        }

        // read from Events.txt
        initialStats = ReadEventFile(args[0]);

        // read from Stats.txt and store to initialStats
        ReadStatsFile(args[1], initialStats);

        // assign number of days
        int baseDays=Integer.parseInt(args[2]);

        // Simulation Engine - each base day
        SimulationEngine baseSimulationEngine = new SimulationEngine(initialStats);
        System.out.println(ANSI_YELLOW + "Generating & logging base events..." + ANSI_RESET);
        baseSimulationEngine.generateEventLogs(baseDays,"day");
        System.out.println(ANSI_YELLOW + "Base Event Logging Complete...\n" + ANSI_RESET);

        // Analysis Engine
        AnalysisEngine baseAnalysysEngine = new AnalysisEngine(initialStats);
        System.out.println(ANSI_YELLOW + "Analyzing Each Base Day's Events..." + ANSI_RESET);
        EventType[] baseStats = baseAnalysysEngine.processEventLogs(baseDays,"day");
        System.out.println(ANSI_YELLOW + "Complete generating baseline training data...\n" + ANSI_RESET);

        Scanner sc = new Scanner(System.in);

        while(true)
        {
            System.out.println("Enter \"StatsFilename\" Days (q to quit): ");
            String line=sc.nextLine();
            if(line.equals("q"))
                break;

            System.out.println(line);
            String elements[] = line.split(" ");

            // check user input
            if(elements.length == 2)
            {
                int liveDays = Integer.parseInt(elements[1]);

                // clone event name, type, min, max, weight
                // but use the new stats
                EventType[] newStats = EventType.cloneEvents(initialStats);

                // read the user input Stats file
                ReadStatsFile(elements[0], newStats);

                // Simulation Engine - each live day
                SimulationEngine liveSimulationEngine = new SimulationEngine(newStats);
                System.out.println(ANSI_YELLOW + "Generating & logging live events..." + ANSI_RESET);
                liveSimulationEngine.generateEventLogs(liveDays,"live_day");
                System.out.println(ANSI_YELLOW + "Live Event Logging Complete...\n" + ANSI_RESET);

                // Alert Engine
                AlertEngine alertEngine = new AlertEngine(baseStats);

                // counting of event inside anomalyCheck
                alertEngine.anomalyCheck(liveDays, "live_day");
            }
        }
    }   // end of main

    // read event types from file
    static EventType[] ReadEventFile(String filename) throws FileNotFoundException
    {
        System.out.println(ANSI_BLUE + "Reading in from " + filename + "..." + ANSI_RESET);
        Scanner file = new Scanner(new FileReader(filename));
        String numLine = file.nextLine();   // skip the first line
        int eventCount = Integer.parseInt(numLine); // count number of events
        EventType[] eventTypes = new EventType[eventCount]; // to store events

        for(int i = 0; i < eventCount; i++)
        {
            // read in from file
            String line = file.nextLine();
            String[] elements = line.split(":");

            // assign value to string variables
            String name = elements[0];
            String type = elements[1];
            String minStr = elements[2];
            String maxStr = elements[3];
            String weightStr = elements[4];

            // set to maximum if empty
            double maxDouble = Double.MAX_VALUE;

            // set to zero if empty
            double weightDouble = 0;
            double minDouble = 0;

            // for continuous event, each day max value is 1440
            if((maxStr.isEmpty()) && ((type.equals("C")) || (type.equals("c"))))
                maxDouble = 1440.0;

            // if not empty
            if(!minStr.isEmpty())
                minDouble = Double.parseDouble(minStr);

            // if not empty
            if(!maxStr.isEmpty())
                maxDouble = Double.parseDouble(maxStr);

            // if not empty
            if(!weightStr.isEmpty())
                weightDouble = Double.parseDouble(weightStr);

            // add event to eventTypes array
            eventTypes[i] = new EventType(name, type.charAt(0),
                                            minDouble, maxDouble, weightDouble);
        }   // end of for-loop
        System.out.println(ANSI_BLUE + "Complete reading event...\n" + ANSI_RESET);
        return eventTypes;
    }   // end of ReadEventFile()

    // read Stats file, update each event mean and std from the Stats file
    static void ReadStatsFile(String filename, EventType[] eventTypes) throws FileNotFoundException
    {
        System.out.println(ANSI_BLUE + "Reading in from " + filename + "..." + ANSI_RESET);
        Scanner file = new Scanner(new FileReader(filename));
        String numLine = file.nextLine();   // skip first line
        int statCount = Integer.parseInt(numLine);  // count number of stats
        for(int i = 0; i < statCount; i++)
        {
            String line = file.nextLine();
            String[] elements = line.split(":");

            String name = elements[0];
            String meanStr = elements[1];
            String stdStr = elements[2];

            double meanDouble = Double.parseDouble(meanStr);
            double stdDouble = Double.parseDouble(stdStr);

            // update mean and std value of the matching event type
            for(int j = 0; j < eventTypes.length; j++)
            {
                if (eventTypes[j].getName().equals(name))
                {
                    eventTypes[j].setMean(meanDouble);
                    eventTypes[j].setStd(stdDouble);
                }
            }   // end of for loop
        }   // end of for loop

        // for debugging
        System.out.println(ANSI_BLUE + "Printing all event info and stats...\n" + ANSI_RESET);
        for(int j = 0; j < eventTypes.length; j++)
        {
            System.out.printf("%-12s: %s%n", "Event Name", eventTypes[j].getName());
            System.out.printf("%-12s: %s%n", "Event Type", eventTypes[j].getType());
            System.out.printf("%-12s: %s%n", "Event Min", eventTypes[j].getMin());
            System.out.printf("%-12s: %s%n", "Event Max", eventTypes[j].getMax());
            System.out.printf("%-12s: %s%n", "Event Weight", eventTypes[j].getWeight());
            System.out.printf("%-12s: %s%n", "Event Mean", eventTypes[j].getMean());
            System.out.printf("%-12s: %s%n%n", "Event Std", eventTypes[j].getStd());
        }
        System.out.println(ANSI_BLUE + "Complete reading event stats...\n" + ANSI_RESET);
    }   // end of ReadStatsFile()
}   // end of IDS class

