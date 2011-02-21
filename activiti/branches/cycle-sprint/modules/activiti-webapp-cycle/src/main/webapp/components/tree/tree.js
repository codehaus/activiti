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
  Activiti.component.Tree = function Tree_constructor(htmlId, nodesJson, containingNavigationTabIndex, connectorId, nodeId, vFolderId, treeId)
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
    
    this._connectorId = connectorId;
    this._nodeId = nodeId;
    this._vFolderId = vFolderId;

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
        if(this.getNodeByConnectorAndId(this._connectorId, this._nodeId)) {
          this.highlightCurrentNode();          
        } else if(this._connectorId && this._nodeId) {
          this.services.repositoryService.loadTree({connectorId: this._connectorId||'', nodeId: this._nodeId||'', vFolderId: this._vFolderId||'', treeId: this._treeId||''});
        }
      } 
    },

    onLoadTreeSuccess: function Tree_onLoadTreeSuccess(response, obj)
    {
      this._nodesJson = response.json;
      this.initTree();
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
          me.services.repositoryService.loadChildNodes({connectorId: node.data.connectorId||'', nodeId: node.data.nodeId||'', vFolderId: node.data.vFolderId||'', treeId: me._treeId||''}, node, fnLoadComplete);
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

        // TODO: (Nils Preusker, 17.2.2011), This is a hard coded implementation of "dynamic" context menu entries, based on the type of the tree node.
        // There are several ways of doing this right in the future:
        // 1) Dynamically load contextr menu when it is invoked. THis includes adding a "context-menu.get" webscript and javascript logic to render it.
        //    The disadvantage is that the context menues would take some time to load, which might be counter intuitive for the user.
        // 2) Add context menu information to the data array that every tree node contains and render a context menu based on that.
        // 
        // Another issue is that the related dialogs should be dynamic as well. Maybe we could use a similar approach like we did for the actions menu.
        
        if(me._treeId == "ps") {
          if(node.data.file) {
            // this.addItems([]);
          } else if(node.data.folder) {
            if(node.data.type && node.data.type == "Management") {
              // TODO: add listener
              this.addItem({ text: "Add new business document...", onclick: { fn: me.onCreateArtifactContextMenuClick, obj: {title: "New business document", node: node}, scope: me } });
            } else if(node.data.type && node.data.type == "Requirements") {
              // TODO: add listener
              this.addItem({ text: "Add new requirement...", onclick: { fn: me.onCreateArtifactContextMenuClick, obj: {title: "New requirement", node: node} , scope: me } });
            }
            this.addItem({ text: "Create Process Solution...", onclick: { fn: me.onCreateProcessSolutionContextMenuClick, obj: node, scope: me } });
          }
        } else {
          if(node.data.file) {
            // this.addItems([]);
          } else if(node.data.folder) {
            this.addItem({ text: "New artifact...", onclick: { fn: me.onCreateArtifactContextMenuClick, obj: {title: "New artifact", node: node}, scope: me } });
            this.addItem({ text: "New folder...", onclick: { fn: me.onCreateFolderContextMenuClick, obj: node, scope: me } });
          }          
        }

        this.render();
      });
      
      if(this._connectorId && this._nodeId) {
        this.highlightCurrentNode();
      }
      
    },

    /**
     * Will fire an Activiti.event.updateArtifactView event so other components may react.
     *
     * @method onClickEvent
     * @param e {object} The click event
     */
    onClickEvent: function Tree_onClickEvent(event)
    {
      this.fireEvent(Activiti.event.updateArtifactView, {activeNavigationTabIndex: this._containingNavigationTabIndex, activeArtifactViewTabIndex: 0, connectorId: event.node.data.connectorId, nodeId: event.node.data.nodeId, vFolderId: event.node.data.vFolderId, label: event.node.label, file: event.node.data.file}, null, true);
    },

    /**
     * This method is invoked when the "Create artifact here..." context menu item is clicked. It returns a new dialog component to
     * provide details for the new artifact.
     *
     * @method onCreateArtifactContextMenuClick
     * @param eventName {string} the name of the event that lead to the invokation of this method
     * @param params {Array} array of parameters that contains the event that lead to the invokation of this method
     * @param node {Object} the tree node that the context menu was invoked on
     * @return {Activiti.component.CreateArtifactDialog} dialog to provide details for the new artifact
     */
    onCreateArtifactContextMenuClick: function Tree_onCreateArtifactContextMenuClick(eventName, params, obj)
    {
      var me = this;
      var fnOnUpload = function(response) {
        var json = YAHOO.lang.JSON.parse(response.responseText);
        me.fireEvent(Activiti.event.updateArtifactView, {activeNavigationTabIndex: me._containingNavigationTabIndex, activeArtifactViewTabIndex: 0, connectorId: json.connectorId, nodeId: json.nodeId, vFolderId: json.vFolderId, label: json.label, file: "true"}, null, true);
      };
      return new Activiti.component.CreateArtifactDialog(this.id, obj.node.data.connectorId, obj.node.data.nodeId, obj.title, fnOnUpload);
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
    onCreateFolderContextMenuClick: function Tree_onCreateFolderContextMenuClick(eventName, params, node)
    {
      return new Activiti.component.CreateFolderDialog(this.id, node.data.connectorId, node.data.nodeId);
    },

    onCreateProcessSolutionContextMenuClick: function Tree_onCreateProcessSolutionContextMenuClick(eventName, params, node) {
      var me = this;
		  var content = document.createElement("div");
      content.innerHTML = '<div class="bd"><form id="' + this.id + '-create-process-solution-form" accept-charset="utf-8"><h1>Create new Process Solution</h1><table><tr><td><label>Name:<br/><input type="text" name="processSolutionName" value="" /></label><br/></td></tr></table></form></div>';      
    
      var dialog = new YAHOO.widget.Dialog(content, 
      {
        fixedcenter: "contained",
        visible: false,
        constraintoviewport: true,
        modal: true,
        hideaftersubmit: false,
        buttons: [
          { text: Activiti.i18n.getMessage("button.ok") , handler: { fn: function CreateFolderDialog_onSubmit(event, dialog) {
              me.services.repositoryService.createProcessSolution(dialog.getData());
              if (dialog) {
                dialog.destroy();
              }
            }, isDefault:true }
          },
          { text: Activiti.i18n.getMessage("button.cancel"), handler: { fn: function CreateFolderDialog_onCancel(event, dialog) {
              dialog.cancel();
            }}
          }
        ]
      });
		  dialog.render(document.body);
		  dialog.show();
    },
    
    onCreateProcessSolutionSuccess: function Tree_onCreateProcessSolutionSuccess(response, obj) {
      if(response.json) {
        this.fireEvent(Activiti.event.updateArtifactView, {activeNavigationTabIndex: this._containingNavigationTabIndex, activeArtifactViewTabIndex: 0, connectorId: response.json.connectorId, nodeId: response.json.nodeId, label: response.json.label, folder: response.json.folder}, null, true);
      }
    },
    
    /**
     * Success callback of the RepositoryService method loadChildNodes. This method gets invoked when the asynchronous request returns. It creates a
     * new TextNode instacne based on the JSON in the response and inserts it into the tree. It also determines the file type and sets the style 
     * attribute of the node accordingly.
     * 
     * @method onLoadChildNodesSuccess
     * @param response the response object that contains the JSON response string
     * @param obj an array of objects that contains the containing node at index 0 and the loadComplete callback of the treeView component at index 1
     */
    onLoadChildNodesSuccess: function Tree_RepositoryService_onLoadChildNodesSuccess(response, obj)
    {
      var me = this;
      if(response.json.authenticationError) {
        return new Activiti.component.AuthenticationDialog(this.id, response.json.repoInError, response.json.authenticationError);
      }
      // Retrieve rest api response
      var treeNodesJson = response.json;

      if(treeNodesJson) {
        for(var i = 0; i<treeNodesJson.length; i++) {
          var node = new YAHOO.widget.TextNode(treeNodesJson[i], obj.parentNode, treeNodesJson[i].expanded);
        }
      }

      // call the fnLoadComplete function that the treeView component provides to 
      // indicate that the loading of the sub nodes was successfull.
      obj.fnLoadComplete();
    },

    /**
     * Failure callback of the RepositoryService method loadChildNodes. This method gets invoked if anything goes wrong while loading a tree node. 
     *
     * @method onLoadChildNodesFailure
     * @param response the response object that contains the JSON response string
     * @param obj an array of objects that contains the containing node at index 0 and the loadComplete callback of the treeView component at index 1
     */
    onLoadChildNodesFailure: function Tree_RepositoryService_onLoadChildNodesFailure(response, obj)
    {
      // TODO: see how we can show a custom error message here.

      // call the trees load complete function anyway to keep the rest of the tree usable.
      obj.fnLoadComplete();
    },

    highlightCurrentNode: function Tree_highlightCurrentNode() {
      var me = this;
      var node = this.getNodeByConnectorAndId(this._connectorId, this._nodeId);
      if(node && (node != this._treeView.currentFocus) ) {
        // if the node isn't already focused this is a browser history event and we manually set focus to the current node
        if(node) {
          node.focus();
        }
      }
    },

    getNodeByConnectorAndId: function Tree_getNodeByConnectorAndId(connectorId, id) {
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