package com.restbusters.util.common;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static FileUtils instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private FileUtils() {
    }

    public static synchronized FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    public static File getFileOnClassPath(String filePath) {
        return getInstance().getFileAsFileFromClassPath(filePath);
    }

    public static byte[] getFileOnClassPathByteArray(String filePath) {
        return getInstance().getFileAsByteArrayFromClassPath(filePath);
    }

    public static String getFileOnClassPathAsString(String filePath) {
        return getInstance().getFileAsStringFromClassPath(filePath);
    }

    private String getFileAsStringFromClassPath(String relativePath) {
        String fileContent = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(relativePath);
            fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
        }
        catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return fileContent;
    }


    private byte[] getFileAsByteArrayFromClassPath(String relativePath) {
        byte[] contentBytes = new byte[0];
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(relativePath);
            contentBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
        }
        catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return contentBytes;
    }

    private File getFileAsFileFromClassPath(String relativePath) {
        return new File(getClass().getClassLoader().getResource(relativePath).getPath());
    }



}
