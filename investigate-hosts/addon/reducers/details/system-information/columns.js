const COLUMNS = {
  'HOST_ENTRIES': {
    field: 'hostFileEntries',
    columns: [
      {
        field: 'ip',
        title: 'investigateHosts.systemInformation.ipAddress',
        width: '10%'
      },
      {
        field: 'hosts',
        title: 'investigateHosts.systemInformation.dnsName',
        width: '80%'
      }
    ]
  },
  'MOUNTED_PATH': {
    field: 'mountedPaths',
    columns: [
      {
        field: 'path',
        title: 'investigateHosts.systemInformation.path',
        width: '20%'
      },
      {
        field: 'fileSystem',
        title: 'investigateHosts.systemInformation.fileSystem',
        width: '10%'
      },
      {
        field: 'remotePath',
        title: 'investigateHosts.systemInformation.remotePath',
        width: '20%'
      },
      {
        field: 'options',
        title: 'investigateHosts.systemInformation.options',
        width: '45%'
      }
    ]
  },
  'NETWORK_SHARES': {
    field: 'networkShares',
    columns: [
      {
        field: 'name',
        title: 'investigateHosts.systemInformation.name',
        width: '10%'
      },
      {
        field: 'description',
        title: 'investigateHosts.systemInformation.description',
        width: '10%'
      },
      {
        field: 'path',
        title: 'investigateHosts.systemInformation.path',
        width: '20%'
      },
      {
        field: 'permissions',
        title: 'investigateHosts.systemInformation.permissions',
        width: '20%'
      },
      {
        field: 'type',
        title: 'investigateHosts.systemInformation.type',
        width: '10%'
      },
      {
        field: 'maxUses',
        title: 'investigateHosts.systemInformation.maxUses',
        width: '5%'
      },

      {
        field: 'currentUses',
        title: 'investigateHosts.systemInformation.currentUses',
        width: '8%'
      }
    ]
  },
  'BASH_HISTORY': {
    field: 'bashHistories',
    columns: [
      {
        field: 'userName',
        title: 'investigateHosts.systemInformation.userName',
        width: '20%'
      },
      {
        field: 'command',
        title: 'investigateHosts.systemInformation.command',
        width: '70%'
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
        width: '20%'
      },
      {
        field: 'companyName',
        title: 'investigateHosts.systemInformation.securityProducts.companyName',
        width: '20%'
      },
      {
        field: 'instance',
        title: 'investigateHosts.systemInformation.securityProducts.instance',
        width: '20%'
      },
      {
        field: 'features',
        title: 'investigateHosts.systemInformation.securityProducts.features',
        width: '20%'
      },
      {
        field: 'type',
        title: 'investigateHosts.systemInformation.securityProducts.type',
        width: '15%'
      },
      {
        field: 'version',
        title: 'investigateHosts.systemInformation.securityProducts.version',
        width: '10%'
      }
    ]
  }
};
export default COLUMNS;
