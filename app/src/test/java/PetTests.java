import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

public class PetTests {

    @Test
    public void petInitTest(){
        Pet buddy = new Pet("k8lin");
        Date today = new Date();
        Assert.assertEquals("Joe Mama's pet", buddy.getName());
        Assert.assertEquals("k8lin",buddy.getOwnerId());
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
        Pet buddy2 = new Pet("k8lin2");
        Assert.assertEquals(true,buddy2.feed());
        Assert.assertEquals(true,buddy2.bathe());
        Assert.assertEquals(true, buddy2.getIsFed());
        Assert.assertEquals(true,buddy2.getIsBathed());
    }

}
