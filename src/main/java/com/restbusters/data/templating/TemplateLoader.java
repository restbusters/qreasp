/*
 * *
 *  * Created by RESTBUSTERS on 6/15/21, 2:01 PM
 *  * @author Ed Vayn
 *  * @project qreasp
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 6/15/21, 2:01 PM
 *  * Last modified @author Sasha Matsaylo 6/15/21, 2:01 PM
 *
 */

package com.restbusters.data.templating;

import freemarker.cache.StringTemplateLoader;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import org.apache.commons.codec.CharEncoding;

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

    public String processTemplate(String templateName, Map<String, Object> data) {
        return TemplateLoaderHelper.processTemplate(freemarkerConfig, templateDirectory, templateName, data);
    }

}
