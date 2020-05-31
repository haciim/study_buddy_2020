// Author: Andrew Calimlim

package studyBuddy;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import java.util.List;


public class Pet {
    /** Instance Variables */
    private String name; // name of the pet
    private int trustLevel; // pet's trust level of the user, ranges from -10 to 10
    private int moodLevel; // pet's mood level, ranges from -10 to 10
    private String color; // pet's color
    private boolean isFed; // has the pet been fed yet today?
    private boolean isBathed; // has the pet been bathed yet today?
    
    /* days recorded at worst trust level */
    private int daysAtWorstTrust;
   
    /* the last date recorded at the worst trust level */
    private Date lastDAWT;
    private Date birthDate; // possible birthday tracker

    /** Constructor */
    public Pet() {
        
        name = "Your pet";
        
        trustLevel = 0;

        moodLevel = 0;

        color = "default";

        isFed = false;
        isBathed = false;

        daysAtWorstTrust = 0;

        birthDate = new Date();

    } 

    /** Getter Methods */

    public String getName(){
        return name;
    }

    public int getTrustLevel(){
        return trustLevel;
    }

    public int getMoodLevel(){
        return moodLevel;
    }

    public String getColor(){
        return color;
    }

    public boolean getIsFed(){
        return isFed;
    }

    public boolean getIsBathed(){
        return isBathed;
    }

    public int getDaysAtWorstTrust(){
        return daysAtWorstTrust;
    } 

    public Date getLastDAWT(){
        return lastDAWT;
    }

    public Date getBirthDate(){
        return birthDate;
    }

    /** Setter Methods */
    // methods that return booleans for fields that are changeable
    // dependent on certain pet conditions and will return false
    // if field cannot change due to condition
    
    public boolean setName(String newName){
        
        if(trustLevel >= 2){
            name = newName;
            return true;
        }

        else{
            return false;
        }
    }

    public void setTrustLevel(int newTrustLevel){
        trustLevel = newTrustLevel;
    }

    public void setMoodLevel(int newMoodLevel){
        moodLevel = newMoodLevel;
    }

    public boolean setColor(String newColor){
        if(trustLevel >= 4){
            color = newColor;
            return true;
        }
        else{
            return false;
        }
    }

    public void setIsFed(boolean newState){
        isFed = newState;
    }

    public void setIsBathed(boolean newState){
        isBathed = newState;
    }

    public void setDaysAtWorstTrust(int n){
        daysAtWorstTrust = n;
    } 

    /** Other Functionality */

    public boolean feed(){
        // check if pet has been fed already
        if(isFed){
            return false;
        }
        else{
            isFed = true;
            return true;
        }
    }

    public boolean bathe(){
        // check if pet has been bathed already
        if(isBathed){
            return false;
        }
        else{
            isBathed = true;
            return true;
        }
    }

    /**
     * isSameWeek - Helper method for trust check
     *
     * Function: Checks if two dates are in the same week
     */
    private boolean isSameWeek(Date d1, Date d2){
        Calendar d1_cal = Calendar.getInstance();
        Calendar d2_cal = Calendar.getInstance();

        d1_cal.setTime(d1);
        d2_cal.setTime(d2);

        boolean sameWeek =
                d1_cal.get(Calendar.WEEK_OF_MONTH) ==
                d2_cal.get(Calendar.WEEK_OF_MONTH)
                &&
                d1_cal.get(Calendar.MONTH) ==
                d2_cal.get(Calendar.MONTH)
                &&
                d1_cal.get(Calendar.YEAR) ==
                d2_cal.get(Calendar.YEAR);

        return sameWeek;
    }

    /**
     * getWPA - Helper method for trust check
     *
     * Function: Calculates the Weekly Productivity Average by:
     *
     * -iterating through the list of recorded sessions
     *
     * -keeping a total sum of the productivity percentages
     * from this week's sessions
     *
     * -keeping a count of this week's sessions
     *
     * -calculating the average by total sum / count
     *
     * Post-condition:
     * returns -1 if no sessions occurred this week
     */
    private double getWPA(){
        //Need a date to check this week
        Date today = new Date();

        // Loading session history
        List<Session> sessions = DataManager.load(List.class);

        int countedSessions = 0;
        double cumulativeSum = 0;
        //iterating through session history
        for(int i = 0; i < sessions.size(); i++){
            Session curSession = sessions.get(i);

            // checking if current session is from this week
            boolean sameWeekQuery = isSameWeek(today, curSession.getStartTime());

            // if session occurred this week, add % productivity to
            // average and count the session
            if(sameWeekQuery){
                cumulativeSum += curSession.getPercentProductive();
                countedSessions++;
            }
        }

        // null version of double
        if(countedSessions == 0){
            return -1;
        }
        else{
            double WPA = cumulativeSum/countedSessions;
            return WPA;
        }
    }

    /**
     * trust Check - main trust check method
     *
     * Function:
     * Updated pet's trust level by calculating the weekly cumulative
     * productivity average
     *
     * Post-condition:
     * Should be called only once a week (end of day Friday or so)
     */

    public void trustCheck(){

        double WPA = getWPA();

        // no need to change pet trust level if no sessions happened that week
        // though it would be weird if the user stopped using the app for a week
        // i guess a vacation?

        if(WPA != -1){
            if(WPA >= 0.5){
                trustLevel++;
            }
            else{
                trustLevel--;
            }
        }
    }

    /**
     * isSameDay - Helper method for mood check
     *
     * Function: Checks if two dates are on the same day
     */

    private boolean isSameDay(Date d1, Date d2){
        Calendar d1_cal = Calendar.getInstance();
        Calendar d2_cal = Calendar.getInstance();

        d1_cal.setTime(d1);
        d2_cal.setTime(d2);

        boolean sameDay = d1_cal.get(Calendar.DAY_OF_YEAR) ==
                d2_cal.get(Calendar.DAY_OF_YEAR) &&
                d1_cal.get(Calendar.YEAR)
                        == d2_cal.get(Calendar.YEAR);

        return sameDay;

    }

    /**
     * getDPA - Helper method for mood check
     *
     * Function: Calculates the Daily Productivity Average by:
     *
     * -iterating through the list of recorded sessions
     *
     * -keeping a total sum of the productivity percentages
     * from today's sessions
     *
     * -keeping a count of today's sessions
     *
     * -calculating the average by total sum / count
     *
     * Post-condition:
     * returns -1 if no sessions occurred today
     */

    private double getDPA(){
        //Need a date to check the day's timer
        Date today = new Date();

        // Loading session history
        List<Session> sessions = DataManager.load(List.class);

        int countedSessions = 0;
        double cumulativeSum = 0;
        //iterating through session history
        for(int i = 0; i < sessions.size(); i++){
            Session curSession = sessions.get(i);

            // checking if current session is from today
            boolean sameDayQuery = isSameDay(today, curSession.getStartTime());

            // if session occurred today, add % productivity to
            // average and count the session
            if(sameDayQuery){
                cumulativeSum += curSession.getPercentProductive();
                countedSessions++;
            }
        }

        // null version of double
        if(countedSessions == 0){
            return -1;
        }
        else{
            double DPA = cumulativeSum/countedSessions;
            return DPA;
        }
    }

    /**
     * moodCheck - main mood check method
     *
     * Function:
     * Checks pet's mood by calculating the daily cumulative
     * productivity average
     *
     * Post-condition:
     * Should be called only once a day (5 PM to 11PM or so)
     */

    public void moodCheck(){
        // getting the daily productivity average
        double DPA = getDPA();

        // no need to change mood if nothing happened today
        if(DPA != -1){
            if(DPA >= 0.5){
                moodLevel++;
            }
            else{
                moodLevel--;
            }
        }
    }

    public void updateLastDAWT(){
        Date now = new Date();
        lastDAWT = now;
    }

    public void worstTrustCheck(){
        // if trust level is at the lowest
        if(trustLevel == -10){
            Date now = new Date();

            //if this is the first day
            if (lastDAWT != null){
                lastDAWT = now;
                daysAtWorstTrust++;
            }


            // this is to punish the user for the days they don't open
            // the app when their pet trust level is at -10

            else{
                //create Calendar objects to compare today to the 
                //last recorded worst day
                Calendar today = Calendar.getInstance();
                today.setTime(now);

                Calendar lastWorstDay = Calendar.getInstance();
                lastWorstDay.setTime(lastDAWT);
                
                int tDay = today.get(Calendar.DAY_OF_YEAR);
                int tYear = today.get(Calendar.YEAR); 
                int lDay = lastWorstDay.get(Calendar.DAY_OF_YEAR);
                int lYear = lastWorstDay.get(Calendar.YEAR); 

                // if today and last recorded worst day are different days
                // Stack overflow told me to do it this way
                if(tDay != lDay && tYear != lYear){
                    // find difference in days between said days
                    long millis = now.getTime()-lastDAWT.getTime();
                    TimeUnit time = TimeUnit.MILLISECONDS;
                    //don't worry this should work unless the user opens the app
                    // after 5,883,516 years
                    int daysAdd = Math.toIntExact(time.toDays(millis));
                    
                    // but just in case things get weird
                    daysAdd = Math.abs(daysAdd);

                    //add the missing days to data
                    daysAtWorstTrust = daysAtWorstTrust + daysAdd;

                    //also today is now the last recorded worst day
                    updateLastDAWT();

                }
            }

        }
    }

    // basically check if it's been several weeks where the pet
    // is at trust level -10
    // aka resetTime
    public boolean isResetTime(){
        
        int maxDaysAtWorst = 21; //blackjack!
        if(daysAtWorstTrust > 21){
            return true;
        }
        else{
            return false;
        }
    }

}