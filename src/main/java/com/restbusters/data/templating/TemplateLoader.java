/*
 * *
 *  * Created by RESTBUSTERS on 6/15/21, 2:01 PM
 *  * @author Ed Vayn
 *  * @project qreasp
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 6/15/21, 2:01 PM
 *
 */

package com.restbusters.data.templating;

import freemarker.cache.StringTemplateLoader;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.codec.CharEncoding;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class TemplateLoader {
    private Configuration freemarkerConfig;

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    private String templateDirectory = "src/test/resources/";

    public TemplateLoader() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerConfig.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
        freemarkerConfig.setDefaultEncoding(CharEncoding.UTF_8);
        freemarkerConfig.setNumberFormat("computer");
        freemarkerConfig.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build());
        freemarkerConfig.setTemplateLoader(new StringTemplateLoader());
    }

    private Template loadTemplate(String templateName, String templatePath) {
        try {
            String templateContent = new String(Files.readAllBytes(Paths.get(templatePath)));
            ((StringTemplateLoader) freemarkerConfig.getTemplateLoader()).putTemplate(templateName, templateContent);
            return freemarkerConfig.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String processTemplate(String templateName, Map<String, Object> data) {
        Template template = loadTemplate(templateName, templateDirectory + templateName);
        try (StringWriter writer = new StringWriter()) {
            template.process(data, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
