export default [{
  tabType: 'IP',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'overview',
      title: 'context.header.overview',
      dataSourceType: 'overview'
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules'
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC'
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents'
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts'
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST'
    },
    {
      field: 'liveConnect',
      title: 'context.header.liveConnect',
      dataSourceType: 'LiveConnect-Ip'
    }
  ],

  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'ENDPOINT',
      title: 'context.toolbar.endpoint'
    },
    {
      field: 'Google Lookup',
      title: 'context.toolbar.googleLookup'
    },
    {
      field: 'VirusTotal Lookup',
      title: 'context.toolbar.virusTotal'
    },
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
      dataSourceType: 'overview'
    },
    {
      field: 'Users',
      title: 'context.header.users',
      dataSourceType: 'Users'

    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents'
    },

    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts'
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST'
    }
  ],
  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'Google Lookup',
      title: 'context.toolbar.googleLookup'
    },
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
      dataSourceType: 'overview'
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules'
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC'
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents'
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts'
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST'
    },
    {
      field: 'liveConnect',
      title: 'context.header.liveConnect',
      dataSourceType: 'liveConnect'
    }

  ],
  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'ENDPOINT',
      title: 'context.toolbar.endpoint'
    },
    {
      field: 'Google Lookup',
      title: 'context.toolbar.googleLookup'
    },
    {
      field: 'VirusTotal Lookup',
      title: 'context.toolbar.virusTotal'
    },
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
      dataSourceType: 'overview'
    },
    {
      field: 'Modules',
      title: 'context.header.modules',
      dataSourceType: 'Modules'
    },
    {
      field: 'IOC',
      title: 'context.header.iioc',
      dataSourceType: 'IOC'
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents'
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts'
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST'
    }
  ],
  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'ENDPOINT',
      title: 'context.toolbar.endpoint'
    },
    {
      field: 'Google Lookup',
      title: 'context.toolbar.googleLookup'
    },
    {
      field: 'VirusTotal Lookup',
      title: 'context.toolbar.virusTotal'
    },
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
      dataSourceType: 'overview'
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents'
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts'
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST'
    }
  ],
  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'Google Lookup',
      title: 'context.toolbar.googleLookup'
    },
    {
      field: 'VirusTotal Lookup',
      title: 'context.toolbar.virusTotal'
    },
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
      dataSourceType: 'overview'
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents'
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts'
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST'
    },
    {
      field: 'liveConnect',
      title: 'context.header.liveConnect',
      dataSourceType: 'liveConnect'
    }
  ],
  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'Google Lookup',
      title: 'context.toolbar.googleLookup'
    },
    {
      field: 'VirusTotal Lookup',
      title: 'context.toolbar.virusTotal'
    },
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
      dataSourceType: 'overview'
    },
    {
      field: 'Incidents',
      title: 'context.header.incidents',
      dataSourceType: 'Incidents'
    },
    {
      field: 'Alerts',
      title: 'context.header.alerts',
      dataSourceType: 'Alerts'
    },
    {
      field: 'Lists',
      title: 'context.header.lists',
      dataSourceType: 'LIST'
    },
    {
      field: 'liveConnect',
      title: 'context.header.liveConnect',
      dataSourceType: 'liveConnect'
    }
  ],

  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'Google Lookup',
      title: 'context.toolbar.googleLookup'
    },
    {
      field: 'VirusTotal Lookup',
      title: 'context.toolbar.virusTotal'
    },
    {
      field: 'Add To List',
      title: 'context.toolbar.addToList',
      componentName: 'add-to-list'
    }
  ]
}
];
