package com.restbusters.util.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;

/**
 * @author Sasha Matsaylo on 9/27/19
 * @project qreasp
 */
public class TestCmdExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    private void test_cmd() throws Exception {
        String command = "ls -ltr";
        String result = CmdExecutor.execToString(command);
        Assert.assertTrue(result.length()>0);
        logger.info(CmdExecutor.execToString(command));

    }


}
