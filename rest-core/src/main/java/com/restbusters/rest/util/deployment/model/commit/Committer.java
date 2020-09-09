
package com.restbusters.rest.util.deployment.model.commit;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class Committer {

    private Boolean active;
    private String displayName;
    private String emailAddress;
    private long id;
    private Links links;
    private String name;
    private String slug;
    private String type;

}
