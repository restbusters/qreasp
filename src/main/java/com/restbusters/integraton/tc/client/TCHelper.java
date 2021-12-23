package com.restbusters.integraton.tc.client;

import com.restbusters.integraton.tc.client.model.post.job.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo
 * @project qreasp
 */
public class TCHelper {

    private TCHelper() { }

    public static PostBuild buildTeamCityTriggerBuildRequest(String projectId, String buildConfigId, @Nullable String branch, @Nullable String message,
                                                      @Nullable Map<String,String> buildProperties) {

        PostBuild postBuild = new PostBuild();
        BuildType buildType = new BuildType();
        buildType.setBuildTypeId(buildConfigId);
        buildType.setProjectId(projectId);
        postBuild.setBuildType(buildType);
        if (StringUtils.isNotBlank(message)) {
            Comment comment = new Comment();
            comment.setText(message);
            postBuild.setComment(comment);
        }
        if (StringUtils.isNotBlank(branch)) {
            Comment comment = new Comment();
            comment.setText(branch);
            postBuild.setBranchName(branch);
        }
        if (MapUtils.isNotEmpty(buildProperties)) {
            Properties properties = new Properties();
            List<PropertyItem> propertyItemList = new ArrayList<>();
            for (Map.Entry<String, String> entry : buildProperties.entrySet()) {
                PropertyItem propertyItem = new PropertyItem();
                propertyItem.setName(entry.getKey());
                propertyItem.setValue(entry.getValue());
                propertyItemList.add(propertyItem);
            }
            properties.setProperty(propertyItemList);
            postBuild.setProperties(properties);
        }
        return postBuild;
    }
}
