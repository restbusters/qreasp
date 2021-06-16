/*
 * *
 *  * Created by RESTBUSTERS on 6/15/21, 2:04 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 6/15/21, 2:04 PM
 *
 */

package com.restbusters.data.templating;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateHashModel;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TemplateTransform {

    private static String baseDir = "src/test/resources";
    private static String templateDir = "/payload/template/";
    private static String dataDir = "/payload/test_data/";
    private static String jsonSource = dataDir + "test_cases_exported.json";
    private static String ftl = "jsonTemplate.ftl";

    public static void main(String[] args) throws Exception {
        TemplateLoader templateLoader = new TemplateLoader();
        templateLoader.setTemplateDirectory(baseDir + templateDir);

        // Transformation from xml to json
        //xmlToJson(fmtManager);

        // Transformation from json to xml
        jsonToJson(templateLoader, jsonSource, ftl );
    }

    private static void jsonToJson(TemplateLoader templateLoader, String inputName, String tempName) throws Exception {
        String input = new String(Files.readAllBytes(Paths.get(baseDir + inputName)));

        Map<String, Object> data = new HashMap<>();
        data.put("input", input);

        TemplateHashModel staticModels = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build().getStaticModels();
        data.put("JsonUtil", staticModels.get(JsonTemplateMapper.class.getName()));

        String output = templateLoader.processTemplate(tempName, data);

        System.out.println(output);
    }
    private static void jsonToSplitJson(TemplateLoader templateLoader, String inputName, String tempName) throws Exception {
        String input = new String(Files.readAllBytes(Paths.get(baseDir + inputName)));

        Map<String, Object> data = new HashMap<>();
        data.put("input", input);

        TemplateHashModel staticModels = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build().getStaticModels();
        data.put("JsonUtil", staticModels.get(JsonTemplateMapper.class.getName()));

        String output = templateLoader.processTemplate(tempName, data);

        System.out.println(output);
    }

   /*public static void xmlToJson(FmtManager templateManager) throws Exception {

       String xmlString = new String(Files.readAllBytes(Paths.get("src/main/resources/test.xml")));
       NodeModel xmlNodeModel = NodeModel.parse(new InputSource(new StringReader(xmlString)));

       Map<String, Object> data = new HashMap<>();
       data.put("xml", xmlNodeModel);

       String json = templateManager.processTemplate("xml2json", data);

       System.out.println(json);
   }*/
}
