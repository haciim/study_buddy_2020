// Author: Andrew Calimlim

package studyBuddy.pet;

import com.example.studdybuddy.R;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class PetAnimation implements Serializable {
    /** Instance Variables */
    private Pet thePet;
    private String curAnimation;
    private String curGif;

    private Date lastFed;
    private Date lastBathed;
    
    //TODO: Subject to change

    // a master list of animations that the pet can do
    final private String[] possibleAnimations =
    {"idle_neutral", "idle_happy", "idle_sad", "feed", "bathing", "study"};
    
    //TODO: Subject to change
    
    // a master list of possible gif file names to display

    /** 
     * They are categorized by animation name [index 0]
     * and color palette [index 1]
     * 
    */

    final private String[][] possibleGifs = 

    {{"idle_neutral", "default", "idle_neutral_default.gif"},
     {"idle_happy", "default", "idle_happy_default.gif"},
     {"idle_sad", "default", "idle_sad_default.gif"},
     {"feeding",  "default", "feeding_default.gif"},
     {"bathing",  "default", "bathing_default.gif"},
     {"studying", "default", "studying_default.gif"}};

    /** Constructor */

    // You need an existing Pet object in order to have a PetAnimation object

    public PetAnimation(Pet theePet){
        thePet = theePet;
        curAnimation = "idle_neutral";
        curGif = "idle_neutral_default.gif"; //idle animation, default color
        lastFed = new Date();
        lastBathed = new Date();
    }

    /** Getter methods */
    public Pet getPet(){
        return thePet;
    }

    public String getCurAnimation(){
        return curAnimation;
    }

    public int getCurGif(){
        return R.raw.idle_neutral_default;
    }

    /** Setter methods */



    // helper search method
    private boolean possibleAnimationsSearch(String key){
        for (int i = 0; i < possibleAnimations.length; i++){
            String curString = possibleAnimations[i];
            if(curString.equals(key)){
                return true;
            }
        }
        return false;
    }

    //returns true on successful set, false on unsuccessful set
    public boolean setCurAnimation(String newAnimation){
        
        //making sure the newAnimation is a valid one
        if(possibleAnimationsSearch(newAnimation)){
            curAnimation = newAnimation;

            // "bathing-default.gif" for example
            String newGif = curAnimation + "_" + thePet.getColor() + ".gif";

            //gotta update the current gif accordingly too!
            // no reason why you would change the current gif directly
            // without changing the animation anyways
            curGif = newGif;
            return true;
        }
        return false;
    }

    /**
     * since we have happy and sad gifs for idle depending on mood level,
     * this handles the different idle animations*/
    public void setIdleType(){

        int curMood = thePet.getMoodLevel();

        // pet's mood > 2
        if(curMood > 2){
            setCurAnimation("idle_happy");
        }
        // if -2 <= pet's mood <= 2
        else if(-2 <= curMood){
            setCurAnimation("idle_neutral");
        }
        //pet mood is less than -2
        else{
            setCurAnimation("idle_sad");
        }
    }

    // pet feeds/bathes itself at a certain time if not fed
    // bathed at certain times

    // only for checking the current hour very quickly
    private int getHourOfDay(){
        Date now = new Date();
        //Stack overflow told me to use this object
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(now);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }



    // should be called at the beginning of every pet screen pull
    public void maintenanceCheck(){

        //i know we're probably not making a screen for this but it doesn't hurt
        thePet.worstTrustCheck();


        Date now = new Date();

        long fedDiff = now.getTime() - lastFed.getTime();
        long fedDiffMin = fedDiff/ (60 * 60 * 1000);

        if (fedDiffMin > 30){
            setIdleType();
        }

        long bathedDiff = now.getTime() - lastBathed.getTime();
        long bathedDiffMin = fedDiff/ (60 * 60 * 1000);
        if (bathedDiffMin > 30){
            setIdleType();
        }

        int curHour = getHourOfDay();

        // checking app from 12am to 9am resets feeding/bathing
        // again, minor cosmetic feature so it doesn't matter too much
        if(curHour <= 9){

            thePet.setIsFed(false);
            thePet.setIsBathed(false);
        }


        if(curHour >= 12 && !thePet.getIsFed()){
            //pet feeds itself after noon

            //do in main i guess
            thePet.feed();
            setCurAnimation("feeding");
            lastFed = new Date();

        }

        if(curHour >= (9 + 12) && !thePet.getIsBathed()){
            //pet bathes itself after 9 pm

            thePet.bathe();
            setCurAnimation("bathing");
            lastBathed = new Date();

        }

    }

}