import java.util.Arrays;

// for reading from/writing to JSON files
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.io.BufferedReader;
import java.io.FileReader;

public class PetAnimation {
    /* Instance Variables */
    private Pet thePet;
    private String curAnimation;
    private String curGif;
    
    //TODO: Subject to change

    // a master list of animations that the pet can do
    final private String[] possibleAnimations =
    {"idle", "feeding", "bathing", "studying"};
    
    //TODO: Subject to change
    
    // a master list of possible gif file names to display

    /** 
     * They are categorized by animation name [index 0]
     * and color palette [index 1]
     * 
    */

    final private String[][] possibleGifs = 

    {{"idle",     "default", "idle-default.gif"},
     {"feeding",  "default", "feeding-default.gif"},
     {"bathing",  "default", "bathing-default.gif"},
     {"studying", "default", "studying-default.gif"}};

    /** Constructor */

    // You need an existing Pet object in order to have a PetAnimation object

    public PetAnimation(Pet theePet){
        thePet = theePet;
        curAnimation = "idle";
        curGif = "idle-default.gif"; //idle animation, default color
    }

    /** Getter methods */
    public Pet getPet(){
        return thePet;
    }

    public String getCurAnimation(){
        return curAnimation;
    }

    public String getCurGif(){
        return curGif;
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
            String newGif = curAnimation + "-" + thePet.getColor() + ".gif";

            //gotta update the current gif accordingly too!
            // no reason why you would change the current gif directly
            // without changing the animation anyways
            curGif = newGif;
            return true;
        }
        return false;

    }
    
    /* Save-load functionality */

    /* While these methods are purposeful, it would make more
    sense to just enact save/load in a main method, rather than
    in an instance of an object like this. See said methods to replicate
    said functionality.
    
    If needed, please use the PetAnimation save-load, as it is a 
    wrapper object for a Pet object. */

    public void saveToJSONFile(){

        Gson gson = new Gson();
        String json = gson.toJson(this);
        try (PrintWriter out = new PrintWriter("PetAnimation.json")) {
            out.println(json);
            System.out.println("PetAnimation.json updated.");
        }
        catch (Exception FileNotFoundException){
            System.out.println("PetAnimation.json could not be created.");
        }
    }

    public void loadFromJSONFile(String fname) throws FileNotFoundException{

        //load functionality should be a main method
  
        String path = "PetAnimation.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        PetAnimation newPetAnimation = gson.fromJson(bufferedReader, PetAnimation.class);     

        //set instance variables of saved object to this object
        thePet = newPetAnimation.getPet();
        curAnimation = newPetAnimation.getCurAnimation();
        curGif = newPetAnimation.getCurGif();
    }
    
    public static void main(String[] args){

    }

    
}