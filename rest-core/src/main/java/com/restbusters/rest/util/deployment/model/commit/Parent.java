
package com.restbusters.rest.util.deployment.model.commit;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("unused")
public class Parent {

    private Author author;
    private long authorTimestamp;
    private Committer committer;
    private long committerTimestamp;
    private String displayId;
    private String id;
    private String message;
    private List<Parent> parents;

}
