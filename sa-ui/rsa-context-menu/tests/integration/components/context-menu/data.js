const contextualMenuJson = {
  'data': [
    {
      'cssClasses': [
        'meta-value-session-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getOpenEventsInNewTab',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   var callbackFn = function(predicateId){\n                               ownerComponent.onAfterDrillCallbackNewTab(predicateId, false);\n                          }\n   ownerComponent.drillDownContextMenu(linkElement, \'=\', callbackFn);\n}\n',
      'id': 'viewListNewTab',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '2',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-session-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-session-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getOpenInEventAnalysis',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   ownerComponent.drillDownReconContextMenu(linkElement);\n}\n',
      'id': 'viewListInNewUI',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '1',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-session-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-geo-map-link'
      ],
      'description': '',
      'disabled': 'true',
      'displayName': 'visualizeGeoMapLabel',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   var callbackFn = function(predicateId){\n                               ownerComponent.onAfterDrillCallbackGeoMapNewTab(predicateId, false);\n                          }\n   ownerComponent.drillDownContextMenu(linkElement, \'=\', callbackFn);\n}\n',
      'id': 'viewGeoMapNewTab',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '5',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-geo-map-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link',
        'nw-event-value'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getLookupInLive',
      'id': 'defaultLiveMenuOption',
      'local': 'true',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '10',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link',
        'nw-event-value'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': '/live/search?metaValue={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'client',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ecat-AgentID',
        'ecat.AgentID',
        'ecat_AgentID',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getECATIoc',
      'groupName': 'externalLookupGroup',
      'id': 'ecatIoc',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '16',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst',
        'ecat.AgentID',
        'client'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'ecatui://{0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'file-hash',
        'file.hash',
        'file_hash'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getGoogle',
      'groupName': 'externalLookupGroup',
      'id': 'googleAction',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '11',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'file.hash',
        'alias.host'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.google.com/search?q={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'googleMalwareDiagnosticsLabel',
      'groupName': 'externalLookupGroup',
      'id': 'googleMalwareAction',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '12',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.google.com/safebrowsing/diagnostic?site={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getCentralOpsWhoisForIPsAndHostnames',
      'groupName': 'externalLookupGroup',
      'id': 'centralOpsWhoisForIPsAndHostnames',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '18',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://centralops.net/co/DomainDossier.aspx?addr={0}&amp;dom_whois=true&amp;dom_dns=true&amp;net_whois=true',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getThreatExpertSearch',
      'groupName': 'externalLookupGroup',
      'id': 'threatExpertSearch',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '24',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.threatexpert.com/reports.aspx?find={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getIPVoidSearch',
      'groupName': 'externalLookupGroup',
      'id': 'ipVoidSearch',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '25',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'orig_ip',
        'ip.all'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.ipvoid.com/scan/{0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'viewInSuspiciousDomainReport',
      'groupName': 'contextGroupDataScience',
      'id': 'viewInSuspiciousDomainReport',
      'local': 'true',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '1',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': '/investigation/ds/domain?metaKey={1}&value={0}&foldDomain=true',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'viewInSuspiciousDNSActivityReport',
      'groupName': 'contextGroupDataScience',
      'id': 'viewInSuspiciousDNSActivityReport',
      'local': 'true',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '2',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': '/investigation/ds/domain?metaKey={1}&value={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'x-grid-row'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'eventReconTitle',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   ownerComponent.reconstructionContextMenu(linkElement);\n}\n',
      'id': 'reconstructionAction',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid'
      ],
      'modules': [
        'investigation'
      ],
      'order': '1',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'x-grid-row'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'x-grid-row'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'viewDetailsNewRecon',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   ownerComponent.reconAnalysisContextMenu(linkElement);\n}\n',
      'id': 'reconAnalysisAction',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid'
      ],
      'modules': [
        'investigation'
      ],
      'order': '10',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'x-grid-row'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'refocusInvestigationInNewTabLabel',
      'groupName': 'investigationGroup',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n    var callbackFn = function(predicateId){\n                    var url = ownerComponent.getBaseDeviceUrl() + \'/navigate/values/\' + predicateId+\'/\';\n                   var overrides = ownerComponent.getOverrideUrlSegment().length > 0? \'/\' +ownerComponent.getOverrideUrlSegment() : \'\'; \n                   var openUrl =url + overrides; \n                    window.open(openUrl);                \n}; \n    ownerComponent.drillDownContextMenu(linkElement, \'=\', callbackFn);\n}\n',
      'id': 'rootDrill',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '1',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'ctxmenu-hash-lookup',
        'ctxmenu.hash.lookup',
        'ctxmenu_hash_lookup'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getHashLookup',
      'id': 'hashLookupAction',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.reconstruction.view.content.ReconstructedEventDataGrid'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '1',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ctxmenu-hash-lookup'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://hashlookup.org/index.php?hash={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getRobtex',
      'groupName': 'externalLookupGroup',
      'id': 'robtexAction',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '13',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.robtex.com/dns/{0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'sansIPHistoryLabel',
      'groupName': 'externalLookupGroup',
      'id': 'sansIPAction',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '14',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://isc.sans.org/ipinfo.html?ip={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'mcAfeeHostnameSiteAdvisorLabel',
      'groupName': 'externalLookupGroup',
      'id': 'mcafeeSiteAdvisorAction',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '15',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.siteadvisor.com/sites/{0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getMalwaredomainlistSearch',
      'groupName': 'externalLookupGroup',
      'id': 'malwaredomainlistSearch',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '20',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.malwaredomainlist.com/mdl.php?search={0}&colsearch=All&quantity=50',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getHostsLookup',
      'id': 'hostLookupAction',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '25',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': '/investigate/hosts?query={1} %3D \'{0}\'',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'contextServiceDefaultAction',
      'id': 'contextServiceDefaultAction',
      'local': 'true',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'false',
      'order': '19',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link'
      ],
      'supportedTab': [],
      'type': 'UAP.common.contextmenu.actions.ContextLookupContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyDrillInNewTabLabel',
      'groupName': 'investigationGroup',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   var callbackFn = function(predicateId){\n                               this.onAfterDrillCallbackNewTab(predicateId, true);\n                          }\n   ownerComponent.drillDownContextMenu(linkElement, \'=\', callbackFn);\n}\n',
      'id': 'drillDownNewTabEquals',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '2',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'viewInHostProfileReport',
      'groupName': 'contextGroupDataScience',
      'id': 'viewInHostProfileReport',
      'local': 'true',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '3',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': '/investigation/ds/source_ip?metaKey={1}&value={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'metaGroupLanguagesGrid'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'changeSelectedToOpenLabel',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   ownerComponent.selectedGrid.changeLanguageView(\'ACTION_OPEN\')\n}\n',
      'id': 'change-meta-view-ACTION_OPEN',
      'moduleClasses': [
        'UAP.investigation.meta.EditMetaGroupForm'
      ],
      'modules': [
        'investigation'
      ],
      'order': '1',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'metaGroupLanguagesGrid'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1',
      'view': 'ACTION_OPEN'
    },
    {
      'cssClasses': [
        'metaGroupLanguagesGrid'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'changeSelectedToClosedLabel',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   ownerComponent.selectedGrid.changeLanguageView(\'ACTION_CLOSE\')\n}\n',
      'id': 'change-meta-view-ACTION_CLOSE',
      'moduleClasses': [
        'UAP.investigation.meta.EditMetaGroupForm'
      ],
      'modules': [
        'investigation'
      ],
      'order': '2',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'metaGroupLanguagesGrid'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1',
      'view': 'ACTION_CLOSE'
    },
    {
      'cssClasses': [
        'metaGroupLanguagesGrid'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'changeSelectedToAutoLabel',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   ownerComponent.selectedGrid.changeLanguageView(\'ACTION_AUTO\')\n}\n',
      'id': 'change-meta-view-ACTION_AUTO',
      'moduleClasses': [
        'UAP.investigation.meta.EditMetaGroupForm'
      ],
      'modules': [
        'investigation'
      ],
      'order': '3',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'metaGroupLanguagesGrid'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1',
      'view': 'ACTION_AUTO'
    },
    {
      'cssClasses': [
        'metaGroupLanguagesGrid'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'changeSelectedToHiddenLabel',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   ownerComponent.selectedGrid.changeLanguageView(\'ACTION_HIDDEN\')\n}\n',
      'id': 'change-meta-view-ACTION_HIDDEN',
      'moduleClasses': [
        'UAP.investigation.meta.EditMetaGroupForm'
      ],
      'modules': [
        'investigation'
      ],
      'order': '4',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'metaGroupLanguagesGrid'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1',
      'view': 'ACTION_HIDDEN'
    },
    {
      'cssClasses': [
        'meta-value-name-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyNotEqualsDrillLabel',
      'groupName': 'investigationGroup',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   var callbackFn = function(predicateId){\n                               ownerComponent.onAfterDrillCallback(predicateId);\n                          }\n   ownerComponent.drillDownContextMenu(linkElement, \'!=\', callbackFn);\n}\n',
      'id': 'drillDownNotEquals',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '4',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'scan',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   var callbackFn = function(predicateId, valueCount, metaAlias){\n                               this.onAfterDrillCallbackMalwareScan(predicateId, valueCount, metaAlias);\n                          }\n   ownerComponent.drillDownContextMenu(linkElement, \'=\', callbackFn);\n}\n',
      'id': 'malwareScanAction',
      'local': 'true',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '16',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst',
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getBFKPassiveDNSCollection',
      'groupName': 'externalLookupGroup',
      'id': 'bfkPassiveDNSCollection',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '17',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all',
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.bfk.de/bfk_dnslogger.html?query={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'ip-all',
        'ip-dst',
        'ip-src',
        'ip.all',
        'ip.dst',
        'ip.src',
        'ip_all',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'orig-ip',
        'orig.ip',
        'orig_ip'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getRobtexIPSearch',
      'groupName': 'externalLookupGroup',
      'id': 'robtexIPSearch',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '22',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'orig_ip',
        'ip.all'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.robtex.com/ip/{0}.html',
      'version': '1'
    },
    {
      'cssClasses': [
        'alias-host',
        'alias.host',
        'alias_host',
        'domain-dst',
        'domain.dst',
        'domain_dst'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getUrlVoidSearch',
      'groupName': 'externalLookupGroup',
      'id': 'urlVoidSearch',
      'local': 'false',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '25',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'alias.host',
        'domain.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': 'http://www.urlvoid.com/scan/{0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'ad-username-dst',
        'ad-username-src',
        'ad.username.dst',
        'ad.username.src',
        'ad_username_dst',
        'ad_username_src',
        'user-dst',
        'user-src',
        'user.dst',
        'user.src',
        'user_dst',
        'user_src',
        'username'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'viewInVPNSessionReport',
      'groupName': 'contextGroupDataScience',
      'id': 'viewInVPNSessionReport',
      'local': 'true',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'openInNewTab': 'true',
      'order': '4',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'username',
        'user.src',
        'user.dst',
        'ad.username.src',
        'ad.username.dst'
      ],
      'type': 'UAP.common.contextmenu.actions.URLContextAction',
      'urlFormat': '/investigation/ds/user?metaKey={1}&value={0}',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link',
        'nw-event-value',
        'nw-event-value-drillable'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'getAddToListLabel',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   var callbackFn = function(predicateId){\n                               ownerComponent.onAfterDrillCallbackAddToList(linkElement,predicateId, false);\n                          }\n   ownerComponent.drillDownListMenu(linkElement, \'=\', callbackFn);\n}\n',
      'id': 'addToList',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid'
      ],
      'modules': [
        'investigation'
      ],
      'order': '26',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link',
        'nw-event-value',
        'nw-event-value-drillable'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link',
        'nw-event-value',
        'nw-event-value-drillable'
      ],
      'description': '',
      'disabled': 'false',
      'displayName': 'getCopy',
      'id': 'copyMetaAction',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel',
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '1',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link',
        'nw-event-value',
        'nw-event-value-drillable'
      ],
      'type': 'UAP.common.contextmenu.actions.CopyMetaContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'meta-value-name-link'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyNotEqualsInNewTabLabel',
      'groupName': 'investigationGroup',
      'handler': 'function(){\n   var ownerComponent = this.getPluginOwnerComponent();\n   var linkElement = this.getContextElement();\n   var callbackFn = function(predicateId){\n                       ownerComponent.onAfterDrillCallbackNewTab(predicateId, true);\n                    }\n   ownerComponent.drillDownContextMenu(linkElement, \'!=\', callbackFn);\n}\n',
      'id': 'drillDownNewTabNotEquals',
      'moduleClasses': [
        'UAP.investigation.navigate.view.NavigationPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '3',
      'path': 'context-actions/investigation',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'meta-value-name-link'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-not-equals'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyNotEqualsDrillLabel',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.drillDownContextMenu(this.getContextElement(),\'!=\');}',
      'id': 'InvestigationEventDrillDownNotEquals',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '2',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-not-equals'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-contains'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyContainsDrillLabel',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.drillDownContextMenu(this.getContextElement(),\'contains\');}',
      'id': 'InvestigationEventDrillDownContains',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '3',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-contains'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-equals'
      ],
      'description': '',
      'disabled': 'true',
      'displayName': 'applyDrillInNewTabLabel',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.drillDownContextMenuNewTab(this.getContextElement(),\'=\');}',
      'id': 'InvestigationEventDrillDownEquals',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '4',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-equals'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-not-equals'
      ],
      'description': '',
      'disabled': 'true',
      'displayName': 'applyNotEqualsInNewTabLabel',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.drillDownContextMenuNewTab(this.getContextElement(),\'!=\');}',
      'id': 'InvestigationEventDrillDownNotEqualsNewTab',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '5',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-not-equals'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-contains'
      ],
      'description': '',
      'disabled': 'true',
      'displayName': 'applyContainsInNewTabLabel',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.drillDownContextMenuNewTab(this.getContextElement(),\'contains\');}',
      'id': 'InvestigationEventDrillDownContainsNewTab',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '6',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-contains'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-equals'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusLabel',
      'groupName': 'refocusGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusDrillDownContextMenu(this.getContextElement(),\'=\');}',
      'id': 'InvestigationEventRefocusEquals',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '1',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-equals'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-not-equals'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusNotEqualsLabel',
      'groupName': 'refocusGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusDrillDownContextMenu(this.getContextElement(),\'!=\');}',
      'id': 'InvestigationEventRefocusNotEquals',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '2',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-not-equals'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-contains'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusContainsLabel',
      'groupName': 'refocusGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusDrillDownContextMenu(this.getContextElement(),\'contains\');}',
      'id': 'InvestigationEventRefocusContains',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '3',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-contains'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'ip-dst',
        'ip-src',
        'ip.dst',
        'ip.src',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'session-split',
        'session.split',
        'session_split',
        'tcp-dstport',
        'tcp-srcport',
        'tcp.dstport',
        'tcp.srcport',
        'tcp_dstport',
        'tcp_srcport',
        'udp-dstport',
        'udp-srcport',
        'udp.dstport',
        'udp.srcport',
        'udp_dstport',
        'udp_srcport'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusSessionSplitsLabel',
      'groupName': 'refocusGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusSessionFragmentsContextMenu(this.getContextElement(),false);}',
      'id': 'InvestigationEventRefocusSplitSessions',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '4',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'tcp.srcport',
        'tcp.dstport',
        'udp.srcport',
        'udp.dstport',
        'session.split'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-equals'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusInNewTabLabel',
      'groupName': 'refocusNewTabGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusDrillDownContextMenuNewTab(this.getContextElement(),\'=\');}',
      'id': 'InvestigationEventRefocusNewTabEquals',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '5',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-equals'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-not-equals'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusNotEqualsInNewTabLabel',
      'groupName': 'refocusNewTabGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusDrillDownContextMenuNewTab(this.getContextElement(),\'!=\');}',
      'id': 'InvestigationEventRefocusNewTabNotEqualsNewTab',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '6',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-not-equals'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'nw-event-value-drillable-contains'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusContainsInNewTabLabel',
      'groupName': 'refocusNewTabGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusDrillDownContextMenuNewTab(this.getContextElement(),\'contains\');}',
      'id': 'InvestigationEventRefocusNewTabContainsNewTab',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '7',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'nw-event-value-drillable-contains'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    },
    {
      'cssClasses': [
        'ip-dst',
        'ip-src',
        'ip.dst',
        'ip.src',
        'ip_dst',
        'ip_src',
        'ipv6-dst',
        'ipv6-src',
        'ipv6.dst',
        'ipv6.src',
        'ipv6_dst',
        'ipv6_src',
        'session-split',
        'session.split',
        'session_split',
        'tcp-dstport',
        'tcp-srcport',
        'tcp.dstport',
        'tcp.srcport',
        'tcp_dstport',
        'tcp_srcport',
        'udp-dstport',
        'udp-srcport',
        'udp.dstport',
        'udp.srcport',
        'udp_dstport',
        'udp_srcport'
      ],
      'description': '',
      'disabled': '',
      'displayName': 'applyRefocusSessionSplitsInNewTabLabel',
      'groupName': 'refocusNewTabGroup',
      'handler': 'function(ctx,evt){\n var ownerComponent = this.getPluginOwnerComponent();\n ownerComponent.refocusSessionFragmentsContextMenu(this.getContextElement(),true);}',
      'id': 'InvestigationEventRefocusNewTabSplitSessionsNewTab',
      'moduleClasses': [
        'UAP.investigation.events.view.EventGrid',
        'UAP.investigation.analysis.view.EventAnalysisPanel'
      ],
      'modules': [
        'investigation'
      ],
      'order': '8',
      'path': 'context-actions/events',
      'pluginRawConfig': '',
      'provider': 'OOTB',
      'scope': [
        'ip.src',
        'ip.dst',
        'ipv6.src',
        'ipv6.dst',
        'tcp.srcport',
        'tcp.dstport',
        'udp.srcport',
        'udp.dstport',
        'session.split'
      ],
      'type': 'UAP.common.contextmenu.actions.AbstractContextAction',
      'version': '1'
    }
  ],
  'message': 'Successful',
  'object': null,
  'success': true,
  'total': 50
};

export { contextualMenuJson };
