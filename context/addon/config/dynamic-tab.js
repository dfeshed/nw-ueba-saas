const datasources = {
  endpoint: {
    field: 'Endpoint',
    title: 'context.header.endpoint',
    dataSourceType: 'Endpoint',
    displayType: 'endpoint',
    details: {
      Machines: {
        field: 'Machines',
        title: 'context.hostSummary.title',
        dataSourceType: 'Machines',
        displayType: 'grid',
        tabRequired: false
      },
      Modules: {
        field: 'Modules',
        title: 'context.header.modules',
        dataSourceType: 'Modules',
        displayType: 'table',
        tabRequired: false
      },
      IOC: {
        field: 'IOC',
        title: 'context.header.iioc',
        dataSourceType: 'IOC',
        displayType: 'table',
        tabRequired: false
      }
    },
    class: 'computer-pc-3',
    tabRequired: true
  },
  incident: {
    field: 'Incidents',
    title: 'context.header.incidents',
    dataSourceType: 'Incidents',
    displayType: 'table',
    class: 'flag-square-2',
    tabRequired: true
  },
  alert: {
    field: 'Alerts',
    title: 'context.header.alerts',
    dataSourceType: 'Alerts',
    displayType: 'table',
    class: 'alarm-sound',
    tabRequired: true
  },
  list: {
    field: 'LIST',
    title: 'context.header.lists',
    dataSourceType: 'LIST',
    displayType: 'table',
    class: 'list-bullets-1',
    tabRequired: true
  },
  archer: {
    field: 'Archer',
    title: 'context.archer.title',
    tabTitle: 'context.header.archer',
    dataSourceType: 'Archer',
    displayType: 'grid',
    tabRequired: true,
    class: 'bow-arrow'
  },
  'liveConnectIp': {
    field: 'LiveConnect-Ip',
    title: 'context.header.liveConnect',
    dataSourceType: 'LiveConnect-Ip',
    displayType: 'liveConnect',
    class: 'network-live',
    tabRequired: true
  },
  liveConnectFile: {
    field: 'LiveConnect-File',
    title: 'context.header.liveConnect',
    dataSourceType: 'LiveConnect-File',
    displayType: 'liveConnect',
    class: 'network-live',
    tabRequired: true
  },
  liveConnectDomain: {
    field: 'LiveConnect-Domain',
    title: 'context.header.liveConnect',
    dataSourceType: 'LiveConnect-Domain',
    displayType: 'liveConnect',
    class: 'network-live',
    tabRequired: true
  },
  user: {
    field: 'Users',
    title: 'context.header.users',
    dataSourceType: 'Users',
    displayType: 'grid',
    class: 'account-circle-1',
    tabRequired: true
  }
};

const toolbar = {
  addToList: {
    field: 'Add/Remove from List',
    title: 'context.toolbar.addToList',
    componentName: 'add-to-list'
  },
  endpoint: {
    field: 'Endpoint',
    title: 'context.tooltip.ecat',
    componentName: 'endpoint'

  },
  investigate: {
    field: 'Investigate',
    title: 'context.tooltip.investigate',
    componentName: 'investigate'
  }
};

export default [{
  tabType: 'IP',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    datasources.list,
    datasources.archer,
    datasources.endpoint,
    datasources.incident,
    datasources.alert,
    datasources.liveConnectIp
  ],
  toolbar: [
    toolbar.addToList,
    toolbar.endpoint,
    toolbar.investigate
  ]
},

{
  tabType: 'USER',
  header: 'context.iiocs.header',
  footer: '',
  title: 'context.iiocs.title',
  columns: [
    datasources.list,
    datasources.user,
    datasources.incident,
    datasources.alert
  ],
  toolbar: [
    toolbar.addToList,
    toolbar.investigate
  ]
},

{
  tabType: 'MAC_ADDRESS',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    datasources.list,
    datasources.endpoint,
    datasources.incident,
    datasources.alert,
    datasources.liveConnect
  ],
  toolbar: [
    toolbar.addToList,
    toolbar.endpoint,
    toolbar.investigate
  ]
},

{
  tabType: 'HOST',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    datasources.list,
    datasources.archer,
    datasources.endpoint,
    datasources.incident,
    datasources.alert
  ],
  toolbar: [
    toolbar.addToList,
    toolbar.endpoint,
    toolbar.investigate
  ]
},

{
  tabType: 'FILE_NAME',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    datasources.list,
    datasources.incident,
    datasources.alert
  ],
  toolbar: [
    toolbar.addToList,
    toolbar.investigate
  ]
},

{
  tabType: 'FILE_HASH',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    datasources.list,
    datasources.incident,
    datasources.alert,
    datasources.liveConnectFile
  ],
  toolbar: [
    toolbar.addToList,
    toolbar.investigate
  ]
},

{
  tabType: 'DOMAIN',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    datasources.list,
    datasources.incident,
    datasources.alert,
    datasources.liveConnectDomain
  ],
  toolbar: [
    toolbar.addToList,
    toolbar.investigate
  ]
}];
