package com.restbusters.util.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 8/3/21
 * @project qreasp
 */
public class RBFileUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    private void readFiles() throws IOException {

        Map<String,String> result = RBFileUtils.getInstance().readFilesAsStringIntoMap("src/test/resources/payload/template", "ftl");

        Assert.assertTrue(MapUtils.isNotEmpty(result));

    }

    @Test
    private void readFilesRecursively() throws IOException {
        String ext[] = { "ftl" };
        List<File> result = RBFileUtils.getInstance().readAllFiles("src/test/resources/payload/template/with-metadata", ext, true);
        Assert.assertTrue(CollectionUtils.isNotEmpty(result));

    }


}
