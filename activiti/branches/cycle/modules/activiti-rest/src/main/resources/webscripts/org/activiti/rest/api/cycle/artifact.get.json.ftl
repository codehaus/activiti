{
  "id": "${artifactId}",
  "contentViews": [
  <#list contentViews as contentView>
    {
      "type": "${contentView.key}",
      "name": "${contentView.name}",
      "content": "${contentView.value?html}"
    }
    <#if contentView_has_next>,</#if>
  </#list>
  ]
}
