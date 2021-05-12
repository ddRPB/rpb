package de.dktk.dd.rpb.core.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FilePathUtilTest {
    @Test
    public void path_with_trailing_slash_passes_through() {
        String dummyPath = "abc/";
        assertTrue(dummyPath.equals(FilePathUtil.verifyTrailingSlash(dummyPath)));
    }

    @Test
    public void trailing_slash_will_be_added() {
        String dummyPath = "abc";
        String dummyPathWithTrailingSlash = dummyPath + "/";
        assertTrue(dummyPathWithTrailingSlash.equals(FilePathUtil.verifyTrailingSlash(dummyPath)));
    }
}