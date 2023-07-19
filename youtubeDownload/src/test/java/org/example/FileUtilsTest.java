package org.example;

import org.junit.*;

public class FileUtilsTest {

    @Test
    public void testHasSolution() throws Exception {
        Assert.assertSame("foo was found somehow", Boolean.FALSE, FileUtils.runScript("dir foo", "."));
    }

}
