<ul>
  <#list processes as process>
  <li class="table">
    <a href="#" rel="processId=${process.id}&diagram=${process.diagramResourceName!''}" title="${process.name}">${process.name} - ${process.version}</a>
  </li>
  </#list>
</ul>
<script type="text/javascript">//<![CDATA[
   new Activiti.component.Processes("${args.htmlid?js_string}").setMessages(${messages});
//]]></script>
