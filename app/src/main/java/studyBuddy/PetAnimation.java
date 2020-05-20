package studyBuddy;


import java.util.Arrays;

// for reading from/writing to JSON files
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.io.BufferedReader;
import java.io.FileReader;

import studyBuddy.Pet;

public class PetAnimation {
    /* Instance Variables */
    private Pet thePet;
    private String curAnimation;
    private String curSprite;
    
    //TODO: Subject to changes
    final private String[] possibleAnimations =
    {"idle", "feeding", "bathing", "studying"};
    
    //TODO: Subject to change
    
    /** The idea behind structuring sprite storage here is that
     * they are categorized by animation name [index 0]
     * and color palette [index 1]
     * 
     * Would recommend storing actual sprite files in a similar
     * file structure (e.g. sprites/idle/default -> id1.png,
     * id2.png, etc.)
    */
    final private String[][] possibleSprites = 

    {{"idle",     "default", "id1", "id2", "id3"},
     {"feeding",  "default", "fd1", "fd2", "fd3"},
     {"bathing",  "default", "bd1", "bd2"},
     {"studying", "default", "sd1", "sd2", "sd3", "sd4"}};

    /** Constructor */

     // it just makes sense for the PetAnimation object
     // to require a PetObject
    public PetAnimation(Pet theePet){
        thePet = theePet;
        curAnimation = "idle";
        curSprite = "id1"; //idle animation, default color, sprite 1
    }

    /** Getter methods */
    public Pet getPet(){
        return thePet;
    }

    public String getCurAnimation(){
        return curAnimation;
    }

    public String getCurSprite(){
        return curSprite;
    }

    /** Setter methods */
    // Precondition: newAnimation is a string in possibleAnimations
    // and possibleSprites
    public void setCurAnimation(String newAnimation){

        curAnimation = newAnimation;

    }

    public void setCurSprite(String newSprite){
        curSprite = newSprite;
    }

    /** Additional functionality */
    public String[] getSpriteLoop(String anim, String color){
        for(int i = 0; i < possibleSprites.length; i++){
            String[] row = possibleSprites[i];
            if(row[0].equals(anim) && 
            row[1].equals(color)){
                return Arrays.copyOfRange(row, 2, row.length);
            }
        }
        return null;
    }

    
    //a rough draft of what sprite animating would be like
    public void run(){
        String color = thePet.getColor();
        String[] sprites = getSpriteLoop(curAnimation, color);

        // TODO: check if app is at pet screen as a boolean
        // NOTE: changing this to true makes run() run infinitely
        boolean atPetScreen = false;

        double animationRate = 1; //seconds per frame

        int i = 0;
        int j = 0;
        long startTime = System.currentTimeMillis();
        while(atPetScreen){
            //resetting the loop
            if(i >= sprites.length){
                i = 0;
            }
            long curTime = System.currentTimeMillis();
            
            // tutorialsPoint told me to do this to calculate
            // elapsed time in seconds
            // 1000 milliseconds rn, 1000F
            double dif = (curTime - startTime)/(animationRate * 1000);
            int d = (int) Math.floor(dif);

            if(d > j){
                curSprite = sprites[i];
                System.out.println(curSprite);
                j++;
                i++;  
            }

        }
    }

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
        curSprite = newPetAnimation.getCurSprite();
    }
    
    public static void main(String[] args) throws FileNotFoundException{
        
    }

    
}