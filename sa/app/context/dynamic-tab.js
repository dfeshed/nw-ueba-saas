export default[{
  tabType: 'IP',
  header: 'context.modules.header',
  footer: ' ',
  title: 'context.modules.title',
  columns: [
    {
      field: 'overview',
      title: 'context.header.overview'
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
      title: 'context.header.liveConnect'
    }
  ],

  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'ECAT',
      title: 'context.toolbar.ecat'
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
      title: 'context.toolbar.addToList'
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
      title: 'context.header.overview'
    },
    {
      field: 'users',
      title: 'context.header.users',
      dataSourceType: 'AD'

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
      title: 'context.toolbar.addToList'
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
      title: 'context.header.overview'
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
      title: 'context.header.liveConnect'
    }

  ],
  toolbar: [
    {
      field: 'Investigate',
      title: 'context.toolbar.investigate'
    },
    {
      field: 'ECAT',
      title: 'context.toolbar.ecat'
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
      title: 'context.toolbar.addToList'
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
      title: 'context.header.overview'
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
      field: 'ECAT',
      title: 'context.toolbar.ecat'
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
      title: 'context.toolbar.addToList'
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
      title: 'context.header.overview'
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
      title: 'context.toolbar.addToList'
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
      title: 'context.header.overview'
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
      title: 'context.header.liveConnect'
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
      title: 'context.toolbar.addToList'
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
      title: 'context.header.overview'
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
      title: 'context.header.liveConnect'
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
      title: 'context.toolbar.addToList'
    }
  ]
}
];
