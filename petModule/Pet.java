import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class Pet {
    /* Instance Variables */ 
    private String name;
    final private String ownerID; 
    private int trustLevel; //-10 to 10
    private int moodLevel; //-10 to 10
    private String color;
    private int[] mStates; //Maintenance State field
    private int daysAtWorstTrust; //Need way to keep track of days at -10
    private Date lastDAWT; //Need way to keep track of days at -10
    final private Date birthDate; // possible birthday tracker?

    /* Constructor */
    public Pet(String userID) {
        /*TODO: get user's name */
        String userName = "Joe Mama";
        this.name = userName + "\'s" + " pet";
        
        this.ownerID = userID;
        
        this.trustLevel = 0;

        this.moodLevel = 0;

        this.color = "default";

        this.mStates = new int[] {0, 0};
        //0th column = has been feed?
        //1st column = has been bathed?
        //values are binary booleans

        this.daysAtWorstTrust = 0;

        this.birthDate = new Date();

    } 

    /* Getter Methods */

    public String getName(){
        return this.name;
    }

    public String getOwnerID(){
        return this.ownerID;

    }

    public int getTrustLevel(){
        return this.trustLevel;
    }

    public int getMoodLevel(){
        return this.trustLevel;
    }

    public String getColor(){
        return this.color;
    }

    // if maintenance state array ever needed
    public int[] getMStates(){
        return this.mStates;
    }

    // get first maintenance state
    public boolean isFed(){
        if(mStates[0] == 1){
            return true;
        }
        else{
            return false;
        }
    }

    // get second maintenance state
    public boolean isBathed(){
        if(mStates[1] == 1){
            return true;
        }
        else{
            return false;
        }
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
    // dependent on certain pet conditions
    
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

    // if setting maintenance state directly is ever needed
    public void setMState(int index, int value){
        mStates[index] = value;
    }

    public void setDaysAtWorstTrust(int n){
        daysAtWorstTrust = n;
    } 

    /* Other Functionality */

    public boolean feed(){
        if(isFed()){
            return false;
        }
        else{
            mStates[0] = 1;
            return true;
        }
    }

    public boolean bathe(){
        if(isBathed()){
            return false;
        }
        else{
            mStates[1] = 1;
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

    // the maintenance states don't need to keep track of days
    // of inactivity or anything, it's just a cute visual
    // feature
    public void mStatesCheck(){
        int curHour = getHourofDay();
        if(curHour >= 12 && !isFed()){
            //pet feeds itself after noon
            //TODO: updatePetAnimation on self-feeding animation
            feed();

        }

        if(curHour >= (9 + 12) && !isBathed()){
            //pet bathes itself after 9 pm
            //TODO: updatePetAnimation on self-bathing animation
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

    public static void main( String[] args){
        //initial testing
        Pet test = new Pet("8675309");
        test.feed();
    }

    
}