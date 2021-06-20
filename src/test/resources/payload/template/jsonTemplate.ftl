<#assign body = JsonUtil.convertJsonToMap(input)>
{
<#list body.projects as project>
    "tests": [
    <#assign issues = JsonUtil.splitMapEntries(project, "issues", "description", "\n", ":")>
    <#assign size = issues?size>
    <#list issues as issue>
        <#assign size = size-1>
        {"testName": "${issue.summary}",
        "parameters": "${issue.description}"
        }<#if size != 0>,
    </#if>
    </#list>
    ]
</#list>
}
