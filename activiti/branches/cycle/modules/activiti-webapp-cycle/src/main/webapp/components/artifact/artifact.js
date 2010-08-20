(function()
{
	/**
	 * Shortcuts
	 */
	var Dom = YAHOO.util.Dom,
			Selector = YAHOO.util.Selector,
			Event = YAHOO.util.Event,
			Pagination = Activiti.util.Pagination,
			$html = Activiti.util.decodeHTML;
	
	/**
	 * Artifact constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {Activiti.component.Artifact} The new component.Artifact instance
	 * @constructor
	 */
	Activiti.component.Artifact = function Artifact_constructor(htmlId)
  {
    Activiti.component.Artifact.superclass.constructor.call(this, "Activiti.component.Artifact", htmlId);
		// Create new service instances and set this component to receive the callbacks
    this.services.repositoryService = new Activiti.service.RepositoryService(this);
    // Listen for events that interest this component
    this.onEvent(Activiti.event.selectTreeLabel, this.onSelectTreeLabelEvent);
    return this;
  };

  YAHOO.extend(Activiti.component.Artifact, Activiti.component.Base,
  {
		/**
		* Fired by YUI when parent element is available for scripting.
		* Template initialisation, including instantiation of YUI widgets and event listener binding.
		*
		* @method onReady
		*/
		onReady: function Artifact_onReady()
		{
			
		},
		
		onSelectTreeLabelEvent: function Artifact_onSelectTreeLabelEvent(event, args) {
			// get the tree node that was selected
			var node = args[1].value.node;
			// get the header el of the content area
			var headerEl = Selector.query("h1", this.id, true);
			if("header-" + node.data.id === headerEl.id) {
				// do nothing... the same node was clicked twice
			} else {
				var tabView = YAHOO.util.Selector.query('div', 'artifact-div', true);
				// check whether an artifact was selected before. If yes, remove the tabView
				if(tabView) {
					var artifactDiv = document.getElementById('artifact-div');
					artifactDiv.removeChild(tabView);
				}
				// Check whether the selected node is a file node. If so, 
				// we can load its data
				if(node.data.file) {
					this.services.repositoryService.loadArtifact(node.data.id);
				}
				// Update the heading that displays the name of the selected node
		  	headerEl.id = "header-" + node.data.id;
				headerEl.innerHTML = node.label;
			}
		},
		
		/**
     * Will display the artifact
     *
     * @method onLoadArtifactSuccess
     * @param response {object} The callback response
     * @param obj {object} Helper object
     */
    onLoadArtifactSuccess: function RepoTree_RepositoryService_onLoadArtifactSuccess(response, obj)
    {
			var tabView = new YAHOO.widget.TabView(); 
			// Retrieve rest api response
      var artifactJson = response.json;
      var firstTab = true;
			// Add a tab for each content representation from the JSON response
			for(var i = 0; i<artifactJson.contentViews.length; i++) {
				if(artifactJson.contentViews[i].type === "img") {
					tabView.addTab( new YAHOO.widget.Tab({
						label: 'Image',
						content: "<div id=\"artifact-image\"><img id=\"" + artifactJson.id + "\" src=\"" + artifactJson.contentViews[i].content + "\" border=0></img></div>",
						active: firstTab
					}));
					firstTab = false;
				}	else {
					tabView.addTab( new YAHOO.widget.Tab({
						label: artifactJson.contentViews[i].name,
						content: "<div id=\"artifact-source\">\n<pre class=\"prettyprint lang-" + artifactJson.contentViews[i].type + "\" >\n" + artifactJson.contentViews[i].content + "\n</pre></div>",
						active: firstTab
					}));
					firstTab = false;
				}			
			}
			tabView.appendTo('artifact-div');
	   	prettyPrint();
    }

	});

})();
