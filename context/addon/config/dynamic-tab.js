export default [{
  tabType: 'IP',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'Machines',
      title: 'context.hostSummary.title',
      dataSourceType: 'Machines',
      displayType: 'grid',
      class: 'network-computers-2',
      tabRequired: false
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules',
      displayType: 'table',
      class: 'graph',
      tabRequired: true
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC',
      displayType: 'table',
      class: 'report-problem-diamond',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      class: 'flag-square-2',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      class: 'alarm-sound',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      class: 'list-bullets-1',
      tabRequired: true
    },
    {
      field: 'Archer',
      title: 'context.archer.title',
      tabTitle: 'context.header.archer',
      dataSourceType: 'Archer',
      displayType: 'grid',
      tabRequired: true,
      class: 'network-connecting'
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
      class: 'network-live',
      tabRequired: true
    }
  ],

  toolbar: [
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
},

{
  tabType: 'USER',
  header: 'context.iiocs.header',
  footer: '',
  title: 'context.iiocs.title',
  columns: [
    {
      field: 'Users',
      title: 'context.header.users',
      dataSourceType: 'Users',
      displayType: 'grid',
      class: 'account-circle-1',
      tabRequired: true

    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      class: 'flag-square-2',
      tabRequired: true
    },

    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      class: 'alarm-sound',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      class: 'list-bullets-1',
      tabRequired: true
    }
  ],
  toolbar: [
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
},

{
  tabType: 'MAC_ADDRESS',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'Machines',
      title: 'context.hostSummary.title',
      dataSourceType: 'Machines',
      displayType: 'grid',
      class: 'network-computers-2',
      tabRequired: false
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules',
      displayType: 'table',
      class: 'graph',
      tabRequired: true
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC',
      displayType: 'table',
      class: 'report-problem-diamond',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      class: 'flag-square-2',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      class: 'alarm-sound',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      class: 'list-bullets-1',
      tabRequired: true
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
      class: 'network-live',
      tabRequired: true
    }

  ],
  toolbar: [
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
},

{
  tabType: 'HOST',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'Machines',
      title: 'context.hostSummary.title',
      dataSourceType: 'Machines',
      class: 'network-computers-2',
      displayType: 'grid',
      tabRequired: false
    },
    {
      field: 'Archer',
      title: 'context.archer.title',
      tabTitle: 'context.header.archer',
      dataSourceType: 'Archer',
      displayType: 'grid',
      tabRequired: true,
      class: 'network-connecting'
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules',
      displayType: 'table',
      class: 'graph',
      tabRequired: true
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC',
      displayType: 'table',
      class: 'report-problem-diamond',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      class: 'flag-square-2',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      class: 'alarm-sound',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      class: 'list-bullets-1',
      tabRequired: true
    }
  ],
  toolbar: [
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
},
{
  tabType: 'FILE_NAME',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      class: 'flag-square-2',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      class: 'alarm-sound',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      class: 'list-bullets-1',
      tabRequired: true
    }
  ],
  toolbar: [
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
},

{
  tabType: 'FILE_HASH',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      class: 'flag-square-2',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      class: 'alarm-sound',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      class: 'list-bullets-1',
      tabRequired: true
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
      class: 'network-live',
      tabRequired: true
    }
  ],
  toolbar: [
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
},

{
  tabType: 'DOMAIN',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      class: 'flag-square-2',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      class: 'alarm-sound',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      class: 'list-bullets-1',
      tabRequired: true
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
      class: 'network-live',
      tabRequired: true
    }
  ],

  toolbar: [
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
}
];
