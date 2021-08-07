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

import com.restbusters.exception.RecordNotFound;
import freemarker.template.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TemplateManager {

    private Configuration freemarkerConfig;
    private List<TemplateHolder> templateHolderList;

    public TemplateManager(String starDir, String[] extension, boolean isRecursive, String metaDataSplitter, String metaDataKeyValueSplitter) {
        this.templateHolderList = TemplateLoaderHelper.bulkTemplateLoader(starDir, extension, true, ";", "=");
        this.freemarkerConfig = TemplateLoaderHelper.getFreeMarkerConfig();
    }

    public String processTemplate(String templateName, String version,  Map<String, Object> data) throws RecordNotFound {
        if(StringUtils.isBlank(templateName) && StringUtils.isBlank(version)){
            throw new IllegalArgumentException("Invalid arguments provide");
        }
        Optional<TemplateHolder> optionalTemplateHolder = findTemplate(templateName, version);
        if(!optionalTemplateHolder.isPresent()){
            throw new RecordNotFound("Not able to find template by given name or version");
        }
        return TemplateLoaderHelper.processTemplate(freemarkerConfig, optionalTemplateHolder.get(), data);
    }

    private Optional<TemplateHolder> findTemplate(String name, String version){
        return this.templateHolderList.stream()
                .filter(templateHolder ->
                    templateHolder.getName().equalsIgnoreCase(name) && templateHolder.getVersion().equalsIgnoreCase(version))
                .findFirst();

    }

    public String processTemplateWithJsonInput(String templateName, String version) throws RecordNotFound {
        if(StringUtils.isBlank(templateName) && StringUtils.isBlank(version)){
            throw new IllegalArgumentException("Invalid arguments provide");
        }
        Optional<TemplateHolder> optionalTemplateHolder = findTemplate(templateName, version);
        if(!optionalTemplateHolder.isPresent()){
            throw new RecordNotFound("Not able to find template by given name or version");
        }
        return TemplateLoaderHelper.jsonToJson(this.freemarkerConfig, optionalTemplateHolder.get());
    }

}
