<#assign el=args.htmlid?js_string/>

<div class="processInstances">
  <div id="${el}-paginator"></div>
  <div id="${el}-datatable"></div>
</div>

<script type="text/javascript">//<![CDATA[
   new Activiti.component.ProcessInstances("${el}").setMessages(${messages});
//]]></script>

