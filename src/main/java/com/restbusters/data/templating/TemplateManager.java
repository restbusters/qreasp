/*
 * *
 *  * Created by RESTBUSTERS on 8/7/21, 2:01 PM
 *  * @author Sasha Matsaylo/
 *  * Reviewed by Ed Vayn
 *  * @project qreasp
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 8/7/21, 2:01 PM
 *
 */

package com.restbusters.data.templating;

import com.restbusters.exception.RecordNotFound;
import com.restbusters.util.common.GenericUtils;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TemplateManager {

    private Configuration freemarkerConfig;
    private List<TemplateHolder> templateHolderList;

    public TemplateManager(String starDir, String[] extension, boolean isRecursive, String metaDataSplitter, String metaDataKeyValueSplitter) {
        this.templateHolderList = TemplateLoaderHelper.bulkTemplateLoader(starDir, extension, isRecursive, metaDataSplitter, metaDataKeyValueSplitter);
        this.freemarkerConfig = TemplateLoaderHelper.getFreeMarkerConfig();
    }

    public String processTemplate(String templateName, String version, Map<String, Object> data) throws RecordNotFound, IOException, TemplateException {
        if (StringUtils.isBlank(templateName) && StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("Invalid arguments provide");
        }
        TemplateHolder templateHolder = findTemplate(templateName, version);
        return TemplateLoaderHelper.processTemplate(freemarkerConfig, templateHolder, data);
    }


    public String processTemplateWithJsonInput(String templateName, String version) throws RecordNotFound, TemplateException, IOException {
        validateTemplateAndVersion(templateName, version);
        TemplateHolder templateHolder = findTemplate(templateName, version);
        return TemplateLoaderHelper.jsonToJson(this.freemarkerConfig, templateHolder);
    }

    public String processTemplateWithJsonInput(String templateName, String version, String jsonPayload) throws RecordNotFound, TemplateException, IOException {
        validateTemplateAndVersion(templateName, version);
        TemplateHolder templateHolder = findTemplate(templateName, version);
        if(StringUtils.isBlank(jsonPayload)){
            throw new IllegalArgumentException("Provided json payload is empty");
        }

        if (!GenericUtils.isJSONValid(jsonPayload)) {
            throw new IllegalArgumentException("Provided json is not valid");
        }

        templateHolder.setInput(jsonPayload);
        return TemplateLoaderHelper.jsonToJson(this.freemarkerConfig, templateHolder);


    }

    private void validateTemplateAndVersion(String templateName, String version) throws RecordNotFound {
        if (StringUtils.isBlank(templateName) && StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("Invalid arguments provide");
        }
    }

    private TemplateHolder findTemplate(String name, String version) throws RecordNotFound {
        Optional<TemplateHolder> optional = null;

            optional = this.templateHolderList.stream()
                    .filter(templateHolder -> templateHolder.getName() != null && templateHolder.getVersion() != null &&
                            templateHolder.getName().equalsIgnoreCase(name) && templateHolder.getVersion().equalsIgnoreCase(version))
                    .findAny();
            if(!optional.isPresent()){
                throw new RecordNotFound("Not able to find template for provided parameters: " + name + " " + version);
            }
        return optional.get();
    }


}
