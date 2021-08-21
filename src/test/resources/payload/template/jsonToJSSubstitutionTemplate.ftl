<#-- name:jsonToJSSubstitutionTemplate -->
<#-- description:jsonToJSSubstitutionTemplate -->
<#-- version:01 -->
<#assign json = JsonUtil.appendBodyWithFooterAndHeader(header, footer, "data", input)>
{
    ${json.header}
    "${json.bodyKey}": ${json.body},
    ${json.footer}
}
