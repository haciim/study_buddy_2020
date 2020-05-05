package studyBuddy;

/**
 * The studyBuddy.Buddy class represents the statistics of the virtual pet
 */

public class Buddy {
    private String name;   // name of the pet
    private int affinity;  // current affinity

    private static final int DEFAULT_AFFINITY = 0;

    /**
     * constructs a new studyBuddy.Buddy with the given name and
     * default affinity
     *
     * @param name - the name of the pet
     */
    public Buddy(String name) {
        this(name, DEFAULT_AFFINITY);
    }

    /**
     * constructs a new studyBuddy.Buddy with the given name and affinity
     *
     * @param name - the name of the pet
     * @param affinity - the affinity of the pet
     */
    public Buddy(String name, int affinity) {
        this.name = name;
        this.affinity = affinity;
    }

    /**
     * sets the pet's name to the given name
     *
     * @param name - the new name of the pet
     */
    public void SetName(String name) {
        this.name = name;
    }

    /**
     * returns the pets name
     *
     * @return the pets current name
     */
    public String GetName() {
        return name;
    }

    /**
     * increments the pets affinity by the given amount
     *
     * @param amount - the amount to change the affinity by
     */
    public void ChangeAffinity(int amount) {
        affinity += amount;
    }

    /**
     * returns the pets current affinity
     *
     * @return the pets current affinity
     */
    public int GetAffinity() {
        return affinity;
    }
}