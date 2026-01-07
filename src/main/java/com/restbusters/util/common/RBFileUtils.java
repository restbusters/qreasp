package com.restbusters.util.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RBFileUtils {

    private static RBFileUtils instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RBFileUtils() {
    }

    public static synchronized RBFileUtils getInstance() {
        if (instance == null) {
            instance = new RBFileUtils();
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
            if (inputStream == null) {
                logger.warn("Resource not found on classpath: {}", relativePath);
                return null;
            }
            fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
        }
        catch (IOException e) {
            logger.error("Error reading file '{}': {}", relativePath, e.getLocalizedMessage());
        }
        return fileContent;
    }


    private byte[] getFileAsByteArrayFromClassPath(String relativePath) {
        byte[] contentBytes = new byte[0];
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(relativePath);
            if (inputStream == null) {
                logger.warn("Resource not found on classpath: {}", relativePath);
                return contentBytes;
            }
            contentBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
        }
        catch (IOException e) {
            logger.error("Error reading file '{}': {}", relativePath, e.getLocalizedMessage());
        }
        return contentBytes;
    }

    private File getFileAsFileFromClassPath(String relativePath) {
        return new File(getClass().getClassLoader().getResource(relativePath).getPath());
    }

    public Map<String,String> readFilesAsStringIntoMap(String directory, String fileType) throws IOException {
        Map<String,String> templateMap = new HashMap<>();
        List<File> files = Files.list(Paths.get(directory))
                .filter(path -> path.toString().endsWith(fileType))
                .map(Path::toFile)
                .collect(Collectors.toList());
        files.forEach(file -> {
            try {
                templateMap.put(file.getName().split("\\.")[0],  org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8.name()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return templateMap;

    }

    public List<File> readAllFiles(String startDir, String extensions[], boolean recursive){
        if(startDir == null){
            throw new IllegalArgumentException("startDir must not be null");
        }
        File directory = new File(startDir);
        Collection<File> files = FileUtils.listFiles(directory, extensions, recursive);
        files.stream().forEach(file -> {
            file.toString();
        });
        return new ArrayList<>(files);
    }



}
