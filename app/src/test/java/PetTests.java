 import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import studyBuddy.Pet;

public class PetTests {

    @Test
    public void initGetTest(){
        Pet buddy = new Pet();
        Date today = new Date();
        Assert.assertEquals("Your pet", buddy.getName());
        Assert.assertEquals(0,buddy.getTrustLevel());
        Assert.assertEquals(0,buddy.getMoodLevel());
        Assert.assertEquals("default",buddy.getColor());
        Assert.assertEquals(false, buddy.getIsFed());
        Assert.assertEquals(false,buddy.getIsBathed());
        Assert.assertEquals(0,buddy.getDaysAtWorstTrust());
        Assert.assertEquals(today,buddy.getBirthDate());
    }

    @Test
    public void petHealthTest(){
        Pet spot = new Pet();
        Assert.assertEquals(true,spot.feed());
        Assert.assertEquals(true,spot.bathe());
        Assert.assertEquals(true, spot.getIsFed());
        Assert.assertEquals(true,spot.getIsBathed());
    }

    @Test
    public void trustTest(){
        Pet spark = new Pet();
        Assert.assertEquals(false, spark.setName("spark"));
        spark.setTrustLevel(2);
        Assert.assertEquals(true,spark.setName("spark"));
        Assert.assertEquals(false, spark.setColor("blue"));
        spark.setTrustLevel(4);
        Assert.assertEquals(true,spark.setColor("blue"));
//        spark.trustCheck();
//        Assert.assertEquals(5,spark.getTrustLevel());
    }

    @Test
    public void moodTest(){
        Pet spock = new Pet();
        Assert.assertEquals(0,spock.getMoodLevel());
//        spock.moodCheck();
//        Assert.assertEquals(1, spock.getMoodLevel());
    }
}