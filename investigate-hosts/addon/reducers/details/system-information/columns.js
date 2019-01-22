const COLUMNS = {
  'HOST_ENTRIES': {
    field: 'hostFileEntries',
    columns: [
      {
        field: 'ip',
        title: 'investigateHosts.systemInformation.ipAddress',
        width: 100
      },
      {
        field: 'hosts',
        title: 'investigateHosts.systemInformation.dnsName',
        width: 300
      }
    ]
  },
  'MOUNTED_PATH': {
    field: 'mountedPaths',
    columns: [
      {
        field: 'path',
        title: 'investigateHosts.systemInformation.path',
        width: 800
      },
      {
        field: 'fileSystem',
        title: 'investigateHosts.systemInformation.fileSystem'
      },
      {
        field: 'remotePath',
        title: 'investigateHosts.systemInformation.remotePath',
        width: 600
      },
      {
        field: 'options',
        title: 'investigateHosts.systemInformation.options',
        width: 1200
      }
    ]
  },
  'NETWORK_SHARES': {
    field: 'networkShares',
    columns: [
      {
        field: 'name',
        title: 'investigateHosts.systemInformation.name',
        width: 100
      },
      {
        field: 'description',
        title: 'investigateHosts.systemInformation.description'
      },
      {
        field: 'path',
        title: 'investigateHosts.systemInformation.path'
      },
      {
        field: 'permissions',
        title: 'investigateHosts.systemInformation.permissions'
      },
      {
        field: 'type',
        title: 'investigateHosts.systemInformation.type'
      },
      {
        field: 'maxUses',
        title: 'investigateHosts.systemInformation.maxUses',
        width: '15%'
      },

      {
        field: 'currentUses',
        title: 'investigateHosts.systemInformation.currentUses'
      }
    ]
  },
  'BASH_HISTORY': {
    field: 'bashHistories',
    columns: [
      {
        field: 'userName',
        title: 'investigateHosts.systemInformation.userName',
        width: 100
      },
      {
        field: 'command',
        title: 'investigateHosts.systemInformation.command',
        width: 300
      }
    ]
  },
  'WINDOWS_PATCHES': {
    field: 'windowsPatches',
    columns: [
      {
        field: 'windowsPatch',
        title: 'investigateHosts.systemInformation.patches',
        width: '50%'
      }
    ]
  },
  'SECURITY_PRODUCTS': {
    field: 'securityProducts',
    columns: [
      {
        field: 'displayName',
        title: 'investigateHosts.systemInformation.securityProducts.displayName',
        width: 100
      },
      {
        field: 'instance',
        title: 'investigateHosts.systemInformation.securityProducts.instance'
      },
      {
        field: 'features',
        title: 'investigateHosts.systemInformation.securityProducts.features'
      },
      {
        field: 'type',
        title: 'investigateHosts.systemInformation.securityProducts.type',
        width: '15%'
      },
      {
        field: 'version',
        title: 'investigateHosts.systemInformation.securityProducts.version'
      }
    ]
  },
  'SECURITY_CONFIGURATION': { columns: [] }
};
export default COLUMNS;
