package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author smatsaylo on 2020-01-07
 * @project dart
 */


public class DeployCandidateChanges extends ManifestModel {


    private Long id;


    private String userName;


    private String fullName;


    private String commitComment;


    private int filesCount;


    private Long deployCandidateId;


    @JsonIgnore
    private DeployCandidate deployCandidate;

}
