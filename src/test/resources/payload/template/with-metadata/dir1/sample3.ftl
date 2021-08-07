<#-- $name=sample3;description=sample3;version=0.1;inputFileName=sampleInput3.json$ -->
<#assign body = JsonUtil.convertJsonToMap(input)>
{
"id": "${body.id}",
"name": "${body.name!'Default Iva'}",
"category": {
"id": "${body.category_id}",
"name": "${body.category_name}"
},
"photoUrls": [
<#assign photoUrls = body.photoUrls>
<#assign size = photoUrls?size>
<#list photoUrls as photoUrl>
    <#assign size = size-1>
    "${photoUrl}"
    <#if size != 0>,
    </#if>
</#list>
],
"tags": [
 <#assign tags = body.tags>
<#assign size = tags?size>
<#list tags as tag>
    <#assign size = size-1>
    {
    "id": "${tag.id}",
    "name": "${tag.name}"
    }
    <#if size != 0>,
    </#if>
</#list>
],
<#if body.whichStatus=="status1">
    "status1": "${body.status1}",
<#elseif body.whichStatus=="status2">
    "status2": "${body.status2}",
<#elseif body.whichStatus=="status3">
    "status3": "${body.status3}",
<#else>
    "status": "status",
</#if>
"time": "${.now?time}"
}