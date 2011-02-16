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
  Activiti.component.Tree = function Tree_constructor(htmlId, nodesJson, containingNavigationTabIndex, treeId)
  {
    Activiti.component.Tree.superclass.constructor.call(this, "Activiti.component.Tree", htmlId);

    // Create new service instances and set this component to receive the callbacks
    this.services.repositoryService = new Activiti.service.RepositoryService(this);

    // Listen for updateArtifactView event in order to be able to expand the tree up to the selected artifact
    this.onEvent(Activiti.event.updateArtifactView, this.onUpdateArtifactView);

    this._treeId = treeId;

    this._nodesJson = nodesJson;
    this._containingNavigationTabIndex = containingNavigationTabIndex;

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
      this.initTree();
    },
    
    /**
     * Event listener for "Activiti.event.updateArtifactView" event, checks whether the tree is 
     * initialized, initializes the tree if it isn't and sets focus to the currently active node
     * if the tree is initialized.
     *
     * @method onTriggerEvent
     * @param event {Object} the event that triggered the invokation of this method
     * @param args {array} an array of arguments containing the object literal of the event at index 1
     */
    onUpdateArtifactView: function Tree_onUpdateArtifactView(event, args)
    {
      this._connectorId = args[1].value.connectorId;
      this._nodeId = args[1].value.nodeId;

      if(this._containingNavigationTabIndex == args[1].value.activeNavigationTabIndex) {
        // alert("the event interests me in tab " + this._containingNavigationTabIndex + "...");
        // this.services.repositoryService.loadTree({connectorId: this._connectorId, nodeId: this._nodeId});          
      }
      
      if(!this._treeView._nodes || !this.getNodeByConnectorAndId(this._connectorId, this._nodeId)) {
        // The tree is not yet initialized
        
      } else {
        // tree is initialized, this is either a regular click on the tree or an event from the browser history manager
        this.highlightCurrentNode();
      }
    },

    initTree: function Tree_initTree()
    {
      var me = this;
      // Define a method to dynamically load tree nodes tp pass it to the tree instance later
      var loadTreeNodes = function (node, fnLoadComplete) {
        if(node.data.connectorId && node.data.nodeId && node.data.connectorId == me._connectorId && node.data.nodeId == me._nodeId) {
          me.highlightCurrentNode();
        }
        if(node.data.file || node.children.length > 0) {
          // TODO: (Nils Preusker, 16.2.2011) check the "node.children.length > 0" part...
          // Don't attempt to load child nodes for artifacts or nodes that are already loaded
          fnLoadComplete();
        } else {
          me.services.repositoryService.loadChildNodes(node, fnLoadComplete, me._treeId);
        }
      };

      // instantiate the TreeView control
      this._treeView = new YAHOO.widget.TreeView(this.id, this._nodesJson);

      // set the callback function to dynamically load child nodes
      // set iconMode to 1 to use the leaf node icon when a node has no children. 
      this._treeView.setDynamicLoad(loadTreeNodes, 1);
      this._treeView.render();

      // Subscribe to the click event of the tree
      this._treeView.subscribe("clickEvent", this.onClickEvent, null, this);
    },

    /**
     * Will fire an Activiti.event.updateArtifactView event so other components may react.
     *
     * @method onClickEvent
     * @param e {object} The click event
     */
    onClickEvent: function RepoTree_onClickEvent (event)
    {
      this.fireEvent(Activiti.event.updateArtifactView, {"connectorId": event.node.data.connectorId, "nodeId": event.node.data.nodeId, "file": event.node.data.file, "label": event.node.label, "activeNavigationTabIndex": this._containingNavigationTabIndex, "activeArtifactViewTabIndex": 0}, null, true);
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

    /**
     * Failure callback of the RepositoryService method loadNodeData. This method gets invoked if anything goes wrong while loading a tree node. 
     *
     * @method onLoadNodeDataFailure
     * @param response the response object that contains the JSON response string
     * @param obj an array of objects that contains the containing node at index 0 and the loadComplete callback of the treeView component at index 1
     */
    onLoadNodeDataFailure: function RepoTree_RepositoryService_onLoadNodeDataFailure(response, obj)
    {
      // TODO: see how we can show a custom error message here.

      // call the trees load complete function anyway to keep the rest of the tree usable.
      obj[1]();
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


// onLoadTreeSuccess: function RepoTree_RepositoryService_onLoadTreeSuccess(response)
//     {
//       var me = this;

  // (Nils Preusker, 16.11.2011)
  // ... just a placeholder to keep the context menu stuff... this still needs to be re-implemented

  // var contextMenuDiv = document.getElementById(this.id + "-cycle-tree-context-menu-div");
  //    if(contextMenuDiv) {
  //      contextMenuDiv.parentNode.removeChild(contextMenuDiv);
  //    }
  // 
  //      me._contextMenu = new YAHOO.widget.ContextMenu(this.id + "-cycle-tree-context-menu-div", {
  //          trigger: this.id
  //        });
  // 
  //    me._contextMenu.render(document.body);
  //    me._contextMenu.subscribe("triggerContextMenu", function (event, menu) {
  //        // retrieve the node the context menu was triggered on
  //        var oTarget = this.contextEventTarget;
  //        var node = me._treeView.getNodeByElement(oTarget);
  //        
  //        // clear existing menu items and set up the context menu according to the current node
  //        this.clearContent();
  //        if(node.data.file) {
  //          // this.addItems([]);
  //        } else if(node.data.folder) {
  //          this.addItem({ text: "New artifact...", value: {connectorId: node.data.connectorId, nodeId: node.data.nodeId}, onclick: { fn: me.onCreateArtifactContextMenuClick, obj: node, scope: me } });
  //          this.addItem({ text: "New folder...", value: {connectorId: node.data.connectorId, nodeId: node.data.nodeId}, onclick: { fn: me.onCreateFolderContextMenuClick, obj: node, scope: me } });
  //        }
  //        this.render();
  //      });
  // 
  //    
  //  },