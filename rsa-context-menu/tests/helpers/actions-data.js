export default {
  data: [{
    urlFormat: 'http://www.google.com/search?q={0}',
    displayName: 'applyRefocusSessionSplitsInNewTabLabel',
    cssClasses: [
      'ip.src',
      'ip.dst'
    ],
    description: '',
    type: 'UAP.common.contextmenu.actions.AbstractContextAction',
    version: '1',
    modules: [
      'investigation'
    ],
    groupName: 'refocusNewTabGroup',
    pluginRawConfig: '',
    disabled: '',
    id: 'InvestigationEventRefocusNewTabSplitSessionsNew',
    moduleClasses: [
      'UAP.investigation.events.view.EventGrid'
    ],
    order: '8'
  }, {
    urlFormat: 'http://www.google.com/search?q={0}',
    displayName: 'applyRefocusSessionSplitsInNewTabLabelCommon',
    cssClasses: [
      'nw-event-value'
    ],
    description: '',
    type: 'UAP.common.contextmenu.actions.AbstractContextAction',
    version: '1',
    modules: [
      'investigation'
    ],
    groupName: 'refocusNewTabGroup',
    pluginRawConfig: '',
    disabled: '',
    id: 'InvestigationEventRefocusNewTabSplitSessions',
    moduleClasses: [
      'UAP.investigation.events.view.EventGrid'
    ],
    order: '8'
  }, {
    handler: 'http://www.google.com/search?q={0}',
    displayName: 'copyMetaAction',
    cssClasses: [
      'nw-event-value'
    ],
    description: '',
    type: 'UAP.common.contextmenu.actions.AbstractContextAction',
    version: '1',
    modules: [
      'investigation'
    ],
    pluginRawConfig: '',
    disabled: '',
    id: 'copyMetaAction',
    moduleClasses: [
      'UAP.investigation.events.view.EventGrid'
    ],
    order: '8'
  }, {
    handler: 'http://www.google.com/search?q={0}',
    displayName: 'contextServiceDefaultAction',
    cssClasses: [
      'nw-event-value'
    ],
    description: '',
    type: 'UAP.common.contextmenu.actions.AbstractContextAction',
    version: '1',
    modules: [
      'investigation'
    ],
    pluginRawConfig: '',
    disabled: '',
    id: 'contextServiceDefaultAction',
    moduleClasses: [
      'UAP.investigation.events.view.EventGrid'
    ],
    order: '8'
  }, {
    urlFormat: 'http://www.google.com/search?q={0}',
    displayName: 'applyRefocusSessionSplitsInNewTabLabelNew',
    cssClasses: [
      'ip.src',
      'ip.dst'
    ],
    description: '',
    type: 'UAP.common.contextmenu.actions.AbstractContextAction',
    version: '1',
    modules: [
      'investigation'
    ],
    pluginRawConfig: '',
    disabled: '',
    id: 'InvestigationEventRefocusNewTabSplitSessionsNewTabNew',
    moduleClasses: [
      'UAP.investigation.events.view.EventGrid'
    ],
    order: '8'
  }]
};