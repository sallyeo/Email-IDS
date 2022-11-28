/*
262 Assignment 3
Members:
Sally (4603229)
Goh Wee Shen (6747747)
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;


public class SimulationEngine
{
    EventType[] eventStats;

    // constructor
    public SimulationEngine(EventType[] eventStats)
    {
        this.eventStats = eventStats;
    }

    // time stamp for events
    String generateTimestamp()
    {
        int h = (int)Math.floor(Math.random()*24);
        int m = (int)Math.floor(Math.random()*60);
        int s = (int)Math.floor(Math.random()*60);

        // "." is used instead of ":" for time format as ":" is already the field separator
        return String.format("%02d.%02d.%02d", h, m, s);
    }

    // generate event logs for days, save each day to each distinct file
    void generateEventLogs(int days, String filename) throws FileNotFoundException
    {
        for(int dayIdx = 0; dayIdx < days; dayIdx++)
        {
            PrintWriter writer = new PrintWriter(new File(filename + dayIdx + ".txt"));

            // to store each line of event and number of occurrence
            List<String> eventList = new ArrayList<String>();

            // generate random values for each event occurrence frequency
            for(int eventIdx = 0; eventIdx < eventStats.length; eventIdx++)
            {
                // continuous event
                if(eventStats[eventIdx].getType() == 'C')
                {
                    // generate double type for this event
                    double occurFrequency = eventStats[eventIdx].getRandValue();

                    // format to 2 dp
                    eventList.add(generateTimestamp()+":"+
                                    eventStats[eventIdx].getName()+":"+
                                    String.format("%.2f",occurFrequency));
                }   // end of if
                else
                {
                    // discrete event
                    // generate number of events for this day, round to integer
                    int events = (int)Math.round(eventStats[eventIdx].getRandValue());

                    for (int loop = 0; loop < events; loop++)
                    {
                        eventList.add(generateTimestamp() + ":" +
                                eventStats[eventIdx].getName() + ":" + 1);
                    }   // end of for loop
                }   // end of else
            }   // end of for loop

            // sort by time
            Collections.sort(eventList);

            // save each string to file
            for(String line  : eventList)
            {
                writer.println(line);
            }
            writer.close();
        }   // end of for-loop
    }   // end of generateEventLogs()
}   // end of SimulationEngine class
