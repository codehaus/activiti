<#function format value>
  <#if !value??>
    <#return "null">
  <#elseif value?is_sequence>
    <#local result>[<#list value as v>${format(v)}<#if v_has_next>, </#if></#list>]</#local>
    <#return result>
  <#elseif value?is_hash>
    <#local result>{<#list value?keys as key>"${key}" : ${format(value[key])}<#if key_has_next>, </#if></#list>}</#local>
    <#return result>
  <#elseif value?is_boolean>
    <#return value?string>
  <#elseif value?is_number>
    <#return value?c>
  <#elseif value?is_date>
    <#return "\"" + iso8601Date(value) + "\"">
  <#else>
    <#return "\"" + value?string + "\"">
  </#if>
</#function>

<#macro formProperties properties>
[
  <#list properties as p>
  <@formProperty p/><#if p_has_next>,</#if>
  </#list>
]
</#macro>

<#macro formProperty p>
{
  "id": "${p.id}",
  "name": <#if p.name??>"${p.name}"<#else>null</#if>,
  "type": {
    "name": "${p.type.name}", 
    "information": {
      <#list p.type.information?keys as key>
      "${key}": ${format(p.type.information[key])}<#if key_has_next>,</#if>
      </#list>
    }
  },
  "value": <#if p.value??>"${p.value}"<#else>null</#if>, 
  "readable": ${p.isReadable()?string},
  "writable": ${p.isWritable()?string},
  "required": ${p.isRequired()?string}
}
</#macro>

