package studyBuddy;// for tracking days at worst trust level

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

// for reading from/writing to JSON files


public class Pet {
    /* Instance Variables */ 
    private String name;
    private String ownerID; 
    private int trustLevel; // ranges from -10 to 10
    private int moodLevel; // ranges from -10 to 10
    private String color;
    private boolean isFed; // maintenance state 1
    private boolean isBathed; // maintenance state 1
    
    /* days recorded at worst trust level */
    private int daysAtWorstTrust;
   
    /* the last date recorded at the worst trust level */
    private Date lastDAWT;
    private Date birthDate; // possible birthday tracker?

    /* Constructor */
    public Pet(String userID) {
        /*TODO: get user's name */
        String userName = "Joe Mama";
        
        name = userName + "\'s" + " pet";
        
        ownerID = userID;
        
        trustLevel = 0;

        moodLevel = 0;

        color = "default";

        isFed = false;
        isBathed = false;

        this.daysAtWorstTrust = 0;

        this.birthDate = new Date();

    }

    /* Getter Methods */

    public String getName(){
        return name;
    }

    public String getOwnerID(){
        return ownerID;

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

    /* Setter Methods */
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

    /* Other Functionality */

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

    public void trustCheck(){
        // TODO: get hours done that week by user
        // and hours user planned on using 
        int committedHours = 10;
        int hoursDone = 10;

        if(hoursDone >= committedHours){
            trustLevel++;
        }
        else{
            trustLevel--;
        }
    }

    public void moodCheck(){
        // TODO: get cumuluative current productivity average
        // or whatever we are checking to change mood

        double avgProd = 0.75;

        if(avgProd >= 0.5){
            moodLevel++;
        }
        else{
            moodLevel--;
        }
    }

    // only for checking the current hour very quickly
    private int getHourofDay(){
        Date now = new Date();
        //Stack overflow told me to use this object
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(now);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    // pet feeds/bathes itself at a certain time if not fed
    // bathed at certain times
    public void maintenanceCheck(){
        int curHour = getHourofDay();

        
        // checking app from 12am to 9am resets feeding/bathing
        // again, minor cosmetic feature so it doesn't matter too much
        if(curHour <= 9){
            
            isFed = false;
            isBathed = false;
        }


        if(curHour >= 12 && !isFed){
            //pet feeds itself after noon
            //TODO: updatePetAnimation on self-feeding animation
            //do in main i guess
            feed();

        }

        if(curHour >= (9 + 12) && !isBathed){
            //pet bathes itself after 9 pm
            //TODO: updatePetAnimation on self-bathing animation
            //do in main i guess
            bathe();

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
        
        //TODO: agree on said variable value
        int maxDaysAtWorst = 21; //blackjack!
        if(daysAtWorstTrust > 21){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static void main( String[] args) throws FileNotFoundException{

    }

    
    
}