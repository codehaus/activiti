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
   * CycleNavigation constructor.
   *
   * @param {String} htmlId The HTML id of the parent element
   * @return {Activiti.component.CycleNavigation} The new component.CycleNavigation instance
   * @constructor
   */
  Activiti.component.CycleNavigation = function CycleNavigation_constructor(htmlId)
  {
    Activiti.component.CycleNavigation.superclass.constructor.call(this, "Activiti.component.CycleNavigation", htmlId);

    // Create new service instances and set this component to receive the callbacks
    this.services.repositoryService = new Activiti.service.RepositoryService(this);

    // Listen for updateArtifactView event in order to be able to switch to the selected tab if needed
    this.onEvent(Activiti.event.updateArtifactView, this.onUpdateArtifactView);

    this.messages = {};
    this._activeNavigationTabIndex = {};
    this._tabView;
    
    return this;
  };

  YAHOO.extend(Activiti.component.CycleNavigation, Activiti.component.Base,
  {
  
    /**
    * Fired by YUI when parent element is available for scripting.
    * Template initialisation, including instantiation of YUI widgets and event listener binding.
    *
    * @method onReady
    */
    onReady: function CycleNavigation_onReady()
    {
      if (!Activiti.event.isInitEvent(Activiti.event.updateArtifactView)) {
        this.fireEvent(Activiti.event.updateArtifactView, {activeNavigationTabIndex: 0, connectorId: "/", nodeId: ""}, null);
      }
    },

    /**
     * Event listener for "Activiti.event.updateArtifactView" event, sets the focus to the currently active tab and 
     * checks whether the contents of the tab are initialized. If not, it loads the contents via the repositoryService.
     *
     * @method onUpdateArtifactView
     * @param event {Object} the event that triggered the invokation of this method
     * @param args {array} an array of arguments containing the object literal of the event at index 1
     */
    onUpdateArtifactView: function RepoTree_onUpdateArtifactView(event, args)
    {
      var me = this;
      
      // Check whether the tab view is already initialized
      if(!this._tabView) {
        // Initialize the tab view
        this._tabView = new YAHOO.widget.TabView();
        
        // Add tab for the "process solutions" tree
        var processSolutionsTreeUrl = Activiti.service.REST_PROXY_URI_RELATIVE + "tree?" + Activiti.service.Ajax.jsonToParamString({connectorId: "PS", nodeId: ""});
        var processSolutionsTab = new YAHOO.widget.Tab({
          label: this.msg("label.process-solutions"), 
          dataSrc: processSolutionsTreeUrl, 
          cacheData: true
        });

        processSolutionsTab.loadHandler.success = function(response) {
          me.onLoadProcessSolutionsTabSuccess(this /* the tab */, response);
        };
        processSolutionsTab.loadHandler.failure = function(response) {
          me.onLoadProcessSolutionsTabFailure(this /* the tab */, response);
        };
        this._tabView.addTab(processSolutionsTab);

        // Add tab for the "repositories" tree
        var repositoriesTreeUrl = Activiti.service.REST_PROXY_URI_RELATIVE + "tree?" + Activiti.service.Ajax.jsonToParamString({connectorId: "/", nodeId: ""});
        var repositoriesTab = new YAHOO.widget.Tab({
          label: this.msg("label.repositories"), 
          dataSrc: repositoriesTreeUrl,
          cacheData: true
        });
        
        repositoriesTab.loadHandler.success = function(response) {
          me.onLoadRepositoriesTabSuccess(this /* the tab */, response);
        };
        repositoriesTab.loadHandler.failure = function(response) {
          me.onLoadRepositoriesTabFailure(this /* the tab */, response);
        };
        this._tabView.addTab(repositoriesTab);
        
        // Append the tab view to a HTML element...
        this._tabView.appendTo(this.id);
        
        // Select the active tab without firing an event (last parameter is 'silent=true')
        this._tabView.set("activeTab", this._tabView.getTab(args[1].value.activeNavigationTabIndex), true);
      }

      // 4) TODO: deal with event handling and the browser history manager (putting the tab change events on the URL)
      // replace the tabViews onActiveTabChange evnet handler with our own one
      // this._tabView.unsubscribe("activeTabChange", this._tabView._onActiveTabChange);
      //       this._tabView.subscribe("activeTabChange", this.onActiveTabChange, null, this);

      if(args[1].value.activeNavigationTabIndex != this._activeNavigationTabIndex) {
        
        
      }
    },

    /**
    * We need to override the Base_setMessage method here so that we can store the messages and pass 
    * them on to the components that are instanciated here.
    *
    * @method setMessages
    */
    setMessages: function CycleNavigation_setMessages(messages) 
    {
      this.messages = messages;
      Activiti.i18n.addMessages(this.messages, this.name);
      return this;
    },

    onLoadProcessSolutionsTabSuccess: function Artifact_onLoadProcessSolutionsTabSuccess(tab, response) 
    {
      var responseJson = YAHOO.lang.JSON.parse(response.responseText);
      // parse response, create tab content and set it to the tab
      
      var tabContent;
      
      // TODO: parse response
      // new Activiti.component.RepoTree(this.id).setMessages(this.messages);
      
      tab.set('content', tabContent);
    },

    onLoadProcessSolutionsTabFailure: function Artifact_onLoadProcessSolutionsTabFailure(tab, response) 
    {
      var responseJson = YAHOO.lang.JSON.parse(response.responseText);
      var tabContent = "<h3>Java Stack Trace:</h3>";
      for(var line in responseJson.callstack) {
        if( line == 1 || (responseJson.callstack[line].indexOf("org.activiti.cycle") != -1) || responseJson.callstack[line].indexOf("org.activiti.rest.api.cycle") != -1) {
          tabContent += "<span class=\"cycle-stack-trace-highlight\">" + responseJson.callstack[line] + "</span>";
        } else {
          tabContent += "<span class=\"cycle-stack-trace\">" + responseJson.callstack[line] + "</span>";
        }
      }
      tab.set('content', tabContent);
      Activiti.widget.PopupManager.displayError(responseJson.message);
    },
    
    onLoadRepositoriesTabSuccess: function Artifact_onLoadRepositoriesTabSuccess(tab, response) 
    {
      var responseJson = YAHOO.lang.JSON.parse(response.responseText);
      // parse response, create tab content and set it to the tab
      
      var tabContent;
      
      // TODO: parse response
      // new Activiti.component.RepoTree(this.id).setMessages(this.messages);
      
      tab.set('content', tabContent);
    },

    onLoadRepositoriesTabFailure: function Artifact_onLoadRepositoriesTabFailure(tab, response) 
    {
      var responseJson = YAHOO.lang.JSON.parse(response.responseText);
      var tabContent = "<h3>Java Stack Trace:</h3>";
      for(var line in responseJson.callstack) {
        if( line == 1 || (responseJson.callstack[line].indexOf("org.activiti.cycle") != -1) || responseJson.callstack[line].indexOf("org.activiti.rest.api.cycle") != -1) {
          tabContent += "<span class=\"cycle-stack-trace-highlight\">" + responseJson.callstack[line] + "</span>";
        } else {
          tabContent += "<span class=\"cycle-stack-trace\">" + responseJson.callstack[line] + "</span>";
        }
      }
      tab.set('content', tabContent);
      Activiti.widget.PopupManager.displayError(responseJson.message);
    }

    // TODO: add browser history manager handling, we need to introduce an additional value "activeNavigationTabIndex" and rename "activeTabIndex" to "activeArtifactViewTabIndex"
    // onActiveTabChange: function CycleNavigation_onActiveTabChange(event)
    // {
    //   var newActiveTabIndex = this._tabView.getTabIndex(event.newValue);
    //   this.fireEvent(Activiti.event.updateArtifactView, {"connectorId": this._connectorId, "repositoryNodeId": this._repositoryNodeId, "isRepositoryArtifact": this._isRepositoryArtifact, "name": this._name, "activeTabIndex": newActiveTabIndex}, null, true);
    //   YAHOO.util.Event.preventDefault(event);
    // }

  });

})();
