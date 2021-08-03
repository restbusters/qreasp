package com.restbusters.util.common;

import com.restbusters.util.common.FileUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sasha Matsaylo on 8/3/21
 * @project qreasp
 */
public class FileUtilsTest {

    @Test
    private void readFiles() throws IOException {

        Map<String,String> result = FileUtils.getInstance().readFilesAsStringIntoMap("src/test/resources/payload/template", "ftl");

        Assert.assertTrue(MapUtils.isNotEmpty(result));

    }

}
