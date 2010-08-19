{
  "id": "${artifact.id}",
  "representations": [
  <#list representations as representation>
    {
      "type": "${representation.type}",
      "name": "${representation.name}",
      <#if representation.contentFetched>
      "content": "<#list representation.content as item>&#${item};</#list>"
      <#else>
      "url": "${contentUrl}"
      </#if>
    }
    <#if representation_has_next>,</#if>
  </#list>
  ]
}
