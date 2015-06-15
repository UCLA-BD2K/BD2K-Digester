import difflib.Patch;

/**
 * Created by Alan on 6/15/2015.
 */
public class DiffHelperTest {

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.Test
    public void testCompare() throws Exception {
        Patch<String> patch = DiffHelper.compare("test/data/diffFile1.txt", "test/data/diffFile2.txt");
        assert(patch != null);
        DiffHelper.printPatch(patch);
    }
}
