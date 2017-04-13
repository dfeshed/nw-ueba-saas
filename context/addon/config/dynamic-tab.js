export default [{
  tabType: 'IP',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview',
      tabRequired: true
    },
    {
      field: 'Machines',
      title: 'context.hostSummary.title',
      dataSourceType: 'Machines',
      displayType: 'grid',
      tabRequired: false
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Archer',
      title: 'context.archer.title',
      dataSourceType: 'Archer',
      displayType: 'grid',
      tabRequired: false
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
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
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview',
      tabRequired: true
    },
    {
      field: 'Users',
      title: 'context.header.users',
      dataSourceType: 'Users',
      displayType: 'grid',
      tabRequired: true

    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      tabRequired: true
    },

    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
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
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview',
      tabRequired: true
    },
    {
      field: 'Machines',
      title: 'context.hostSummary.title',
      dataSourceType: 'Machines',
      displayType: 'grid',
      tabRequired: false
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
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
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview',
      tabRequired: true
    },
    {
      field: 'Machines',
      title: 'context.hostSummary.title',
      dataSourceType: 'Machines',
      displayType: 'grid',
      tabRequired: false
    },
    {
      field: 'Archer',
      title: 'context.archer.title',
      dataSourceType: 'Archer',
      displayType: 'grid',
      tabRequired: false
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
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
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
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
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
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
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview',
      tabRequired: true
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LIST',
      title: 'context.header.lists',
      dataSourceType: 'LIST',
      displayType: 'table',
      tabRequired: true
    },
    {
      field: 'LiveConnect-Ip',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip',
      displayType: 'liveConnect',
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
