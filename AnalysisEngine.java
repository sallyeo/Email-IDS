/*
262 Assignment 3
Members:
Sally (4603229)
Goh Wee Shen (6747747)
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnalysisEngine
{
    EventType[] eventStats;     // to store each line in each day.txt

    // constructor
    public AnalysisEngine(EventType[] eventTypes)
    {
        this.eventStats=eventTypes;
    }

    // count events for day from a file
    public double [] countDayEvents(String filename) throws FileNotFoundException
    {
        // each index for each event frequency count
        double eventFrequencyCounts[]=new double[eventStats.length];

        Scanner file = new Scanner(new FileReader(filename));

        // loop thru each line
        while(file.hasNextLine())
        {
            String line = file.nextLine();

            // each line separated into 3 components
            String[] element = line.split(":");

            if(element.length != 3)
                continue;

            // assign values read from each line to string variables
            String timestamp = element[0];  // timestamp not needed later
            String eventName = element[1];
            String frequencyStr = element[2]; // used to count number of events

            // convert string to double
            double value = Double.parseDouble(frequencyStr);

            // loop through types of event read from Events.txt and Stats.txt
            for(int eventIdx = 0; eventIdx < eventStats.length; eventIdx++)
            {
                if (eventStats[eventIdx].getName().equals(eventName))
                {
                    eventFrequencyCounts[eventIdx] += value;
                }   // end of if
            }   // end of for-loop
        }   // end of while loop
        return eventFrequencyCounts;
    }   // end of countDayEvents
    
    // generate new event stats calculated based on the simulated events
    public EventType[] processEventLogs(int days, String filename) throws FileNotFoundException
    {
        List<double[]> allDaysEachEventCounts = new ArrayList<>();  // to store number of each event each day

        System.out.println(IDS.ANSI_YELLOW + "Print totals of each event of each day..." + IDS.ANSI_RESET);
        // loop all days
        for(int dayIdx = 0; dayIdx < days; dayIdx++)
        {
            // counting daily event quantity
            double eventFrequencyCounts[]= countDayEvents(filename + dayIdx +".txt");

            // adding each day event count to arraylist
            allDaysEachEventCounts.add(eventFrequencyCounts);

            // save total number of event for each day
            PrintWriter pw = new PrintWriter(new File("total_" + filename + dayIdx + ".txt"));

            System.out.println("\nDay " + dayIdx);

            // loop through the types of event again
            for(int eventIdx = 0; eventIdx < eventStats.length; eventIdx++)
            {
                // for debugging
                System.out.printf("%-14s: %s%n", eventStats[eventIdx].getName(), eventFrequencyCounts[eventIdx]);

                // write to file
                pw.println(eventStats[eventIdx].getName() + ":" + eventFrequencyCounts[eventIdx]);
            }   // end of inner for-loop

            pw.close();
        }   // end of outer for-loop

        // clone same events but update the mean and std
        EventType[] baselineStats = EventType.cloneEvents(eventStats);
        {
            PrintWriter pw = new PrintWriter(new File("baseline_stats.txt"));
            pw.println(eventStats.length);
            System.out.println(IDS.ANSI_YELLOW + "\nCalculating baseline mean & std..." + IDS.ANSI_RESET);
            System.out.println(IDS.ANSI_YELLOW + "Compare given stats vs baseline stats......\n" + IDS.ANSI_RESET);

            // loop through types of event again
            for(int eventIdx = 0; eventIdx < eventStats.length; eventIdx++)
            {
                // sum all values to get mean
                double sum = 0;
                double sumDifferences = 0;

                // loop through each day
                for(int dayIdx = 0; dayIdx < days; dayIdx++)
                {
                    sum += allDaysEachEventCounts.get(dayIdx)[eventIdx];
                }

                // calculate mean value
                double mean = sum/days;
                String type = String.valueOf(baselineStats[eventIdx].getType());
                if (type == "C")
                    mean = Math.round(mean * 100.00) / 100.00;  // convert to 2 dp
                else
                    mean = (Math.round(mean)) * 100.00/100.00;

                // loop through each day
                for(int dayIdx = 0; dayIdx < days; dayIdx++)
                {
                    // sum of square difference from the mean
                    sumDifferences += (allDaysEachEventCounts.get(dayIdx)[eventIdx] - mean) * (allDaysEachEventCounts.get(dayIdx)[eventIdx] - mean);
                }

                // square root of the variance
                double std = Math.sqrt(sumDifferences/days);
                std = Math.round(std * 100.00) / 100.00;  // convert to 2 dp

                // each loop update event std and mean from calculated data
                baselineStats[eventIdx].setMean(mean);
                baselineStats[eventIdx].setStd(std);

                // writing to baseline_stats.txt
                pw.println(eventStats[eventIdx].getName() + ":" + mean + ":" + std + ":");

                // compare initial Stats and baseline Stats
                System.out.printf("%-14s: %s%n", "Event Name", eventStats[eventIdx].getName());
                System.out.printf("%-14s: %s%n", "given mean", eventStats[eventIdx].getMean());
                System.out.printf("%-14s: %s%n", "baseline mean", mean);
                System.out.printf("%-14s: %s%n", "given std", eventStats[eventIdx].getStd());
                System.out.printf("%-14s: %s%n%n", "baseline std", std);
            }
            pw.close();
        }
        return baselineStats;
    }   // end of processEventLogs
} // end of AnalysisEngine class
