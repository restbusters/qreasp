<#-- $name=sample2;description=sample2;version=02;inputFileName=sampleInput2.json$ -->
<#assign body = JsonUtil.convertJsonToMap(input)>
{
<#list body.projects as project>
    "tests": [
    <#assign issues = JsonUtil.splitTestParametersEntries(project, "issues", "description", "\n", ":")>
    <#assign size = issues?size>
    <#list issues as issue>
        <#assign size = size-1>
        {"testName": "${issue.summary}",
        "parameters": [${issue.description}]
        }<#if size != 0>,
    </#if>
    </#list>
    ]
</#list>
}

