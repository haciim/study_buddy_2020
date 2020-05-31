import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import studyBuddy.Pet;

public class PetTests {

    private int MOODSCALE = 10;
    private int TRUSTSCALE =10;
    private Date today = new Date();

    @Test
    public void initGetTest(){
        Pet buddy = new Pet();
        Date today = new Date();
        Assert.assertEquals("Your pet", buddy.getName());
        Assert.assertEquals(0,buddy.getTrustLevel());
        Assert.assertEquals(0,buddy.getMoodLevel());
        Assert.assertEquals("default",buddy.getColor());

        buddy.setIsFed(false);
        buddy.setIsBathed(false);
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

        spot.setIsBathed(false);
        spot.setIsFed(false);
        Assert.assertEquals(false, spot.getIsFed());
        Assert.assertEquals(false,spot.getIsBathed());

        spot.setIsFed(true);
        spot.setIsBathed(true);
        Assert.assertEquals(false,spot.feed());
        Assert.assertEquals(false,spot.bathe());

    }

    @Test
    public void trustTest(){
        Pet spark = new Pet();
        //test naming
        Assert.assertEquals(false, spark.setName(null));
        Assert.assertEquals(false,spark.setName(""));
        Assert.assertEquals(false, spark.setName("spark"));
        spark.setTrustLevel(2);
        Assert.assertEquals(false, spark.setName(null));
        Assert.assertEquals(false,spark.setName(""));
        Assert.assertEquals(true,spark.setName("spark"));

        //test color setting
        Assert.assertEquals(false, spark.setColor(null));
        Assert.assertEquals(false,spark.setColor(""));
        Assert.assertEquals(false, spark.setColor("blue"));
        spark.setTrustLevel(4);
        Assert.assertEquals(false, spark.setColor(null));
        Assert.assertEquals(false,spark.setColor(""));
        Assert.assertEquals(true,spark.setColor("blue"));

        //test trustcheck around bounds
//        spark.setTrustLevel(TRUSTSCALE);
//        spark.trustCheck();
//        Assert.assertTrue(spark.getTrustLevel()==TRUSTSCALE || spark.getTrustLevel()==TRUSTSCALE-1);
//        spark.setTrustLevel(TRUSTSCALE*-1);
//        spark.trustCheck();
//        Assert.assertTrue(spark.getTrustLevel()==TRUSTSCALE*-1 || spark.getTrustLevel()==(TRUSTSCALE*-1)-1);

        //test trust scale bounds
        spark.setTrustLevel(0);
        Assert.assertEquals(0,spark.getTrustLevel());
        spark.setTrustLevel(TRUSTSCALE);
        Assert.assertEquals(TRUSTSCALE, spark.getTrustLevel());
        spark.setTrustLevel(TRUSTSCALE+2);
        Assert.assertEquals(TRUSTSCALE, spark.getTrustLevel());
        spark.setTrustLevel(TRUSTSCALE *-1);
        Assert.assertEquals(TRUSTSCALE*-1,spark.getTrustLevel());
        spark.setTrustLevel((TRUSTSCALE*-1)-2);
        Assert.assertEquals(TRUSTSCALE *-1,spark.getTrustLevel());

        //test days of bad trust
        spark.setDaysAtWorstTrust(5);
        Assert.assertEquals(5,spark.getDaysAtWorstTrust());
        spark.setDaysAtWorstTrust(0);
        Assert.assertEquals(0,spark.getDaysAtWorstTrust());
        spark.setDaysAtWorstTrust(-3);
        Assert.assertEquals(0,spark.getDaysAtWorstTrust());

        spark.setTrustLevel((TRUSTSCALE*-1)+1);
        spark.setDaysAtWorstTrust(0);
        spark.worstTrustCheck();
        Assert.assertEquals(0,spark.getDaysAtWorstTrust());
        spark.setDaysAtWorstTrust(1);
        spark.setTrustLevel(TRUSTSCALE*-1);
//        spark.worstTrustCheck();
//        Assert.assertEquals(2,spark.getDaysAtWorstTrust());
//        Assert.assertEquals(today,spark.getLastDAWT());
    }

    @Test
    public void moodTest(){
        Pet spock = new Pet();
        //basic mood setting
        spock.setMoodLevel(0);
        Assert.assertEquals(0,spock.getMoodLevel());

        //test mood scale bounds
        spock.setMoodLevel(MOODSCALE);
        Assert.assertEquals(MOODSCALE,spock.getMoodLevel());
        spock.setMoodLevel(MOODSCALE+2);
        Assert.assertEquals(MOODSCALE,spock.getMoodLevel());
        spock.setMoodLevel(MOODSCALE*-1);
        Assert.assertEquals(MOODSCALE*-1,spock.getMoodLevel());
        spock.setMoodLevel((MOODSCALE*-1)-2);
        Assert.assertEquals(MOODSCALE*-1,spock.getMoodLevel());

        //test mood check bounds
//        spock.setMoodLevel(MOODSCALE);
//        spock.moodCheck();
//        Assert.assertTrue(spock.getMoodLevel()==MOODSCALE || spock.getMoodLevel()==MOODSCALE-1);
//        spock.setMoodLevel(MOODSCALE*-1);
//        spock.moodCheck();
//        Assert.assertTrue(spock.getMoodLevel()==MOODSCALE*-1 || spock.getMoodLevel()==(MOODSCALE*-1)-1);
    }
}

