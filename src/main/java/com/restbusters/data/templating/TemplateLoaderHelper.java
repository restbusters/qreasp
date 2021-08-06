package com.restbusters.data.templating;

import com.restbusters.util.common.Constant;
import com.restbusters.util.common.GenericUtils;
import com.restbusters.util.common.RBFileUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 6/11/21
 * @project qreasp
 */

public class TemplateLoaderHelper {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private static Template loadTemplate(Configuration freemarkerConfig, String templateName, String templatePath) {
        try {
            String templateContent = new String(Files.readAllBytes(Paths.get(templatePath)));
            ((StringTemplateLoader) freemarkerConfig.getTemplateLoader()).putTemplate(templateName, templateContent);
            return freemarkerConfig.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String processTemplate(Configuration freemarkerConfig, String templateDirectory, String templateName, Map<String, Object> data) {
        Template template = loadTemplate(freemarkerConfig, templateName, templateDirectory + templateName);
        try (StringWriter writer = new StringWriter()) {
            template.process(data, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String,String>> bulkTemplateLoader(String startDir, String[] extensions, boolean isRecursive, String metaDataSplitter, String metaDataKeyValueSplitter){
        List<File> result = RBFileUtils.getInstance().readAllFiles(startDir, extensions, isRecursive);
        List<Map<String,String>> templates = new ArrayList<>();
        result.stream().forEach(file -> {
            try {
                String  template = FileUtils.readFileToString(file);
                String templateMetaData = GenericUtils.RegexMatcher(template, Constant.TEMPLATE_METADATA_REGEX.toString(), 1);
                Map<String,String> templateMap = new HashMap<>(GenericUtils.splitToMap(metaDataSplitter, metaDataKeyValueSplitter, templateMetaData));
                templateMap.put("template", template);
                templates.add(templateMap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return templates;
    }
}



