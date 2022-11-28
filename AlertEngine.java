/*
262 Assignment 3
Members:
Sally (4603229)
Goh Wee Shen (6747747)
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class AlertEngine
{
    EventType[] baseStats;

    // constructor
    public AlertEngine(EventType[] baseStats)
    {
        this.baseStats=baseStats;
    }

    // test alerts for days with anomalies
    public void anomalyCheck(int days,String filename) throws FileNotFoundException
    {
        PrintWriter writer = new PrintWriter(new File("anomaly_analysis.txt"));
        AnalysisEngine analysisEngine = new AnalysisEngine(baseStats);

        boolean alert = false;

        // calculate threshold
        double sumWeights = 0;
        
        for(int eventIdx = 0; eventIdx < baseStats.length; eventIdx++)
        {
            sumWeights += baseStats[eventIdx].getWeight();
        }   // end of for-loop

        // alertTrigger is the threshold
        double alertTrigger = 2 * sumWeights;
        System.out.println(IDS.ANSI_RED + "Starting alert analysis..." + IDS.ANSI_RESET);
        System.out.println(IDS.ANSI_RED + "Threshold: " + alertTrigger + "\n" + IDS.ANSI_RESET);

        for(int dayIdx = 0; dayIdx < days; dayIdx++)
        {
            // get event totals from file for this day
            double eventFrequencyCounts[] = analysisEngine.countDayEvents(filename + dayIdx + ".txt");

            // sum all dailyCounter contributions
            double dailyCounter = 0;

            // counter for each event
            System.out.println("Counter for Day " + dayIdx);
            for(int eventIdx = 0; eventIdx < baseStats.length; eventIdx++)
            {
                // calculate dailyCounter contribution of each type
                // then add them up
                // login counter + Time online counter + Email sent counter + Email opened counter + Emails deleted counter
                double eventDailyCounter = Math.abs((eventFrequencyCounts[eventIdx] - baseStats[eventIdx].getMean()) * baseStats[eventIdx].getWeight() / baseStats[eventIdx].getStd());
                System.out.printf("%-29s: %s%n", baseStats[eventIdx].getName(),  eventDailyCounter);
                dailyCounter += eventDailyCounter;
            }
            dailyCounter = Math.round(dailyCounter * 100.00) / 100.00;  // convert to 2 dp
            System.out.printf("%-27s%s : %s%n%n", "Total Daily Counter of day", dayIdx, dailyCounter);
            writer.printf("%-21s%s: %s%n", "Total Counter of day", dayIdx, dailyCounter);

            if(dailyCounter > alertTrigger)
            {
                System.out.println(IDS.ANSI_RED + "Anomaly detected on day "+ dayIdx + " anomaly weights " + dailyCounter + IDS.ANSI_RESET + "\n");

                alert = true;
            }
        }   // end of for-loop
        writer.close();

        if(!alert)
        {
            System.out.println(IDS.ANSI_GREEN + "No Anomaly detected" + IDS.ANSI_RESET);
        }
    }   // end of anomalyCheck
}   // end of AlertEngine class
