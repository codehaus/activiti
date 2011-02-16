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
   * Tree constructor.
   *
   * @param {String} htmlId The HTML id of the parent element
   * @return {Activiti.component.Tree} The new component.Tree instance
   * @constructor
   */
  Activiti.component.Tree = function Tree_constructor(htmlId)
  {
    Activiti.component.Tree.superclass.constructor.call(this, "Activiti.component.Tree", htmlId);

    // Create new service instances and set this component to receive the callbacks
    this.services.repositoryService = new Activiti.service.RepositoryService(this);

    // Listen for updateArtifactView event in order to be able to expand the tree up to the selected artifact
    this.onEvent(Activiti.event.updateArtifactView, this.onUpdateArtifactView);

    this._treeView = {};
    this._dialog = {};
    
    this._contextMenu = {};
    
    this._connectorId = "";
    this._nodeId = "";

    return this;
  };

  YAHOO.extend(Activiti.component.Tree, Activiti.component.Base,
  {
  
    /**
    * Fired by YUI when parent element is available for scripting.
    * Template initialisation, including instantiation of YUI widgets and event listener binding.
    *
    * @method onReady
    */
    onReady: function Tree_onReady()
    {

    },
    
    /**
     * Success callback of the RepositoryService method loadTree. This method is invoked when the asynchronous request to load the tree
     * returns, it takes the JSON response and creates a YUI TreeView instance with a dynamic load function, specific context menus for
     * the different nodes and a click event listener to fire an event for other components to listen to.
     *
     * @method onLoadTreeSuccess
     * @param response {object} The callback response
     */
    onLoadTreeSuccess: function RepoTree_RepositoryService_onLoadTreeSuccess(response)
    {
      var me = this;

      // Handle authentication errors:
      // If the server reports that there are repositories in the users configuration that need a username and password to log in,
      // we'll show a dialog that prompts the user to provide his username and password for each of these repositories.
      if(response.json.authenticationError) {
        return new Activiti.component.AuthenticationDialog(this.id, response.json.repoInError, response.json.authenticationError);
      }

      // Login is OK, we can start drawing the tree...
      var treeNodesJson = response.json;
      var loadTreeNodes = function (node, fnLoadComplete) {
        if(node.data.connectorId && node.data.nodeId && node.data.connectorId == me._connectorId && node.data.nodeId == me._nodeId) {
          me.highlightCurrentNode();
        }
        if(node.data.file || node.children.length > 0) {
          // Don't attempt to load child nodes for artifacts
          fnLoadComplete();
        } else {
          me.services.repositoryService.loadNodeData(node, fnLoadComplete);
          // TODO: see if there is a way to define a timeout even if the server returns a HTTP 500 status
          //timeout: 7000
        }
      };

      // instantiate the TreeView control
      me._treeView = new YAHOO.widget.TreeView(this.id, treeNodesJson);

      // set the callback function to dynamically load child nodes
      // set iconMode to 1 to use the leaf node icon when a node has no children. 
      me._treeView.setDynamicLoad(loadTreeNodes, 1);
      me._treeView.render();

      me._treeView.subscribe("clickEvent", this.onLabelClick, null, this);

      var contextMenuDiv = document.getElementById(this.id + "-cycle-tree-context-menu-div");
      if(contextMenuDiv) {
        contextMenuDiv.parentNode.removeChild(contextMenuDiv);
      }

		  me._contextMenu = new YAHOO.widget.ContextMenu(this.id + "-cycle-tree-context-menu-div", {
		      trigger: this.id
		    });

      me._contextMenu.render(document.body);
      me._contextMenu.subscribe("triggerContextMenu", function (event, menu) {
          // retrieve the node the context menu was triggered on
          var oTarget = this.contextEventTarget;
          var node = me._treeView.getNodeByElement(oTarget);
          
          // clear existing menu items and set up the context menu according to the current node
          this.clearContent();
          if(node.data.file) {
            // this.addItems([]);
          } else if(node.data.folder) {
            this.addItem({ text: "New artifact...", value: {connectorId: node.data.connectorId, nodeId: node.data.nodeId}, onclick: { fn: me.onCreateArtifactContextMenuClick, obj: node, scope: me } });
            this.addItem({ text: "New folder...", value: {connectorId: node.data.connectorId, nodeId: node.data.nodeId}, onclick: { fn: me.onCreateFolderContextMenuClick, obj: node, scope: me } });
          }
          this.render();
        });

      var reloadLink = document.createElement('a');
      reloadLink.setAttribute('id', this.id + '-tree-refresh-link');
      reloadLink.setAttribute('class', 'tree-refresh-link')
      reloadLink.setAttribute('href', "javascript:location.reload();");
      reloadLink.innerHTML = "refresh tree";
      document.getElementById(this.id).appendChild(reloadLink);
    },

    /**
     * This method is invoked when the "Create artifact here..." context menu item is clicked. It returns a new dialog component to
     * provide details for the new artifact.
     *
     * @method onCreateArtifactContextMenuClick
     * @param eventName {string} the name of the event that lead to the invokation of this method
     * @param params {Array} array of parameters that contains the event that lead to the invokation of this method
     * @param node the tree node that the context menu was invoked on
     * @return {Activiti.component.CreateArtifactDialog} dialog to provide details for the new artifact
     */
    onCreateArtifactContextMenuClick: function RepoTree_onCreateArtifactContextMenuClick(eventName, params, node)
    {
      return new Activiti.component.CreateArtifactDialog(this.id, node.data.connectorId, node.data.nodeId);
    },

    /**
     * This method is invoked when the "Create folder here..." context menu item is clicked. It returns a new dialog component to
     * provide details for the new folder.
     *
     * @method onCreateArtifactContextMenuClick
     * @param eventName {string} the name of the event that lead to the invokation of this method
     * @param params {Array} array of parameters that contains the event that lead to the invokation of this method
     * @param node the tree node that the context menu was invoked on
     * @return {Activiti.component.CreateFolderDialog} dialog to provide details for the new folder
     */
    onCreateFolderContextMenuClick: function RepoTree_onCreateFolderContextMenuClick(eventName, params, node)
    {
      return new Activiti.component.CreateFolderDialog(this.id, node.data.connectorId, node.data.nodeId);
    },

    /**
     * Success callback of the RepositoryService method loadNodeData. This method gets invoked when the asynchronous request returns. It creates a
     * new TextNode instacne based on the JSON in the response and inserts it into the tree. It also determines the file type and sets the style 
     * attribute of the node accordingly.
     * 
     * @method onLoadNodeDataSuccess
     * @param response the response object that contains the JSON response string
     * @param obj an array of objects that contains the containing node at index 0 and the loadComplete callback of the treeView component at index 1
     */
    onLoadNodeDataSuccess: function RepoTree_RepositoryService_onLoadNodeDataSuccess(response, obj)
    {
      var me = this;
      if(response.json.authenticationError) {
        return new Activiti.component.AuthenticationDialog(this.id, response.json.repoInError, response.json.authenticationError);
      }
      // Retrieve rest api response
      var treeNodesJson = response.json;

      if(treeNodesJson) {
        for(var i = 0; i<treeNodesJson.length; i++) {
          var node = new YAHOO.widget.TextNode(treeNodesJson[i], obj[0], treeNodesJson[i].expanded);
        }
      }

      // call the fnLoadComplete function that the treeView component provides to 
      // indicate that the loading of the sub nodes was successfull.
      obj[1]();
    },

    // TODO: See how we can handle failures
    // onLoadNodeDataFailure: function RepoTree_RepositoryService_onLoadNodeDataFailure(response, obj)
    //     {
    //       
    //       
    //     },

    /**
     * Will fire an Activiti.event.updateArtifactView event so other components may display the node
     *
     * @method onLabelClick
     * @param e {object} The click event
     */
    onLabelClick: function RepoTree_onLabelClick (event)
    {
  
      // Map the node properties to the event value object (value object property -> node property):
      // - nodeId -> node.data.nodeId
      // - isRepositoryArtifact -> node.data.file
      // - name -> node.label

      this.fireEvent(Activiti.event.updateArtifactView, {"connectorId": event.node.data.connectorId, "nodeId": event.node.data.nodeId, "file": event.node.data.file, "name": event.node.label, "activeArtifactViewTabIndex": 0}, null, true);
    },

    /**
     * Event listener for "Activiti.event.updateArtifactView" event, sets the focus to the currently active node in the tree.
     *
     * @method onUpdateArtifactView
     * @param event {Object} the event that triggered the invokation of this method
     * @param args {array} an array of arguments containing the object literal of the event at index 1
     */
    onUpdateArtifactView: function RepoTree_onUpdateArtifactView(event, args)
    {
      this._connectorId = args[1].value.connectorId;
      this._nodeId = args[1].value.nodeId;
      if(!this._treeView._nodes || !this.getNodeByConnectorAndId(this._connectorId, this._nodeId)) {
        // tree is not yet initialized, we are coming from an external URL
        this.services.repositoryService.loadTree({connectorId: this._connectorId, nodeId: this._nodeId});
      } else {
        // tree is initialized, this is either a regular click on the tree or an event from the browser history manager
        this.highlightCurrentNode();
      }
    },

    highlightCurrentNode: function RepoTree_highlightCurrentNode() {
      var me = this;
      var node = this.getNodeByConnectorAndId(this._connectorId, this._nodeId);
      if(node && (node != this._treeView.currentFocus) ) {
        // if the node isn't already focused this is a browser history event and we manually set focus to the current node
        if(node) {
          node.focus();
        }
      }
    },

    getNodeByConnectorAndId: function TreeView_getNodeByConnectorAndId(connectorId, id) {
      var nodes = this._treeView.getNodesBy( function(node) {
        if(node.data.connectorId && node.data.nodeId && node.data.connectorId === connectorId && node.data.nodeId === id) {
          return true;
        }
        return false;
      });
      return nodes ? nodes[0] : null;
    }

  });

})();
