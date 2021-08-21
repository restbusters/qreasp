/*
 * *
 *  * Created by RESTBUSTERS on 6/15/21, 2:04 PM
 *  * @author Ed Vayn
 *  * @project qreasp
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 6/15/21, 2:04 PM
 *
 */

package com.restbusters.data.templating;

import com.restbusters.data.templating.generation.JsonGenerationTemplateMapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateHashModel;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TemplateTransform {

    private String baseDir = "src/test/resources";
    private String templateDir = "/payload/template/";
    private String dataDir = "/payload/test_data/";
    private String jsonSource = dataDir + "test_cases_exported.json";
    private String templateName = "jsonTestParamTemplate.ftl";

    public static void main(String[] args) throws Exception {
        TemplateLoader templateLoader = new TemplateLoader();
        TemplateTransform tf = new TemplateTransform();
        templateLoader.setTemplateDirectory(tf.getBaseDir() + tf.getTemplateDir());
        //Transformation from a json to a different json target
        String output = tf.jsonToJson(templateLoader, tf.getJsonSource(), tf.getTemplateName());
        System.out.println(output);
    }
    public String jsonToJson(TemplateLoader templateLoader, String inputName, String tempName) {
        String output = null;
        try {
            String input = new String(Files.readAllBytes(Paths.get(baseDir + inputName)));
            Map<String, Object> data = new HashMap<>();
            data.put("input", input);
            TemplateHashModel staticModels = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build().getStaticModels();
            data.put("JsonUtil", staticModels.get(JsonTemplateMapper.class.getName()));
            output = templateLoader.processTemplate(tempName, data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }

    public void jsonToSplitJson(TemplateLoader templateLoader, String inputName, String tempName) throws Exception {
        String input = new String(Files.readAllBytes(Paths.get(baseDir + inputName)));
        Map<String, Object> data = new HashMap<>();
        data.put("input", input);
        TemplateHashModel staticModels = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build().getStaticModels();
        data.put("JsonUtil", staticModels.get(JsonTemplateMapper.class.getName()));
        String output = templateLoader.processTemplate(tempName, data);
        System.out.println(output);
    }

    public String setJsonBody(TemplateLoader templateLoader, String input, String tempName) {
        String output = null;
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("input", input);
            TemplateHashModel staticModels = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build().getStaticModels();
            data.put("JsonUtil", staticModels.get(JsonGenerationTemplateMapper.class.getName()));
            output = templateLoader.processTemplate(tempName, data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }

    public String setJsonBodyWithFooterAndHeader(TemplateLoader templateLoader, String input,
                                                 String footer, String header, String tempName) {
        String output = null;
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("input", input);
            data.put("footer", footer);
            data.put("header", header);
            TemplateHashModel staticModels = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build().getStaticModels();
            data.put("JsonUtil", staticModels.get(JsonGenerationTemplateMapper.class.getName()));
            output = templateLoader.processTemplate(tempName, data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }
    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getJsonSource() {
        return jsonSource;
    }

    public void setJsonSource(String jsonSource) {
        this.jsonSource = jsonSource;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
