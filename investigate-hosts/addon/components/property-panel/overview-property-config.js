export default [
  {
    sectionName: 'Groups',
    fields: [
      {
        field: 'groupPolicy.groups',
        labelKey: 'groups'
      }
    ]
  },
  {
    sectionName: 'Users',
    prefix: 'machine.users',
    multiOption: true,
    fields: [
      {
        field: 'name',
        labelKey: 'machine.users.name',
        showRightClick: true
      },
      {
        field: 'sessionId',
        labelKey: 'machine.users.sessionId'
      },
      {
        field: 'administrator',
        labelKey: 'machine.users.isAdministrator'
      },
      {
        field: 'groups',
        labelKey: 'machine.users.groups'
      }
    ]
  },
  {
    sectionName: 'Network Interfaces',
    prefix: 'machineIdentity.networkInterfaces',
    multiOption: true,
    fields: [
      {
        field: 'name',
        labelKey: 'machineIdentity.networkInterfaces.name'
      },
      {
        field: 'macAddress',
        labelKey: 'machineIdentity.networkInterfaces.macAddress'
      },
      {
        field: 'ipv4',
        labelKey: 'machineIdentity.networkInterfaces.ipv4',
        showRightClick: true
      },
      {
        field: 'ipv6',
        labelKey: 'machineIdentity.networkInterfaces.ipv6',
        showRightClick: true
      },
      {
        field: 'gateway',
        labelKey: 'machineIdentity.networkInterfaces.gateway'
      },
      {
        field: 'dns',
        labelKey: 'machineIdentity.networkInterfaces.dns'
      },
      {
        field: 'promiscuous',
        labelKey: 'machineIdentity.networkInterfaces.promiscuous'
      }
    ]
  },
  {
    sectionName: 'Operating System',
    fields: [
      {
        field: 'machineIdentity.operatingSystem.description',
        labelKey: 'machineIdentity.operatingSystem.description'
      },
      {
        field: 'machineIdentity.operatingSystem.buildNumber',
        labelKey: 'machineIdentity.operatingSystem.buildNumber'
      },
      {
        field: 'machineIdentity.operatingSystem.servicePack',
        labelKey: 'machineIdentity.operatingSystem.servicePack'
      },
      {
        field: 'machineIdentity.operatingSystem.kernelName',
        labelKey: 'machineIdentity.operatingSystem.kernelName'
      },
      {
        field: 'machineIdentity.operatingSystem.kernelRelease',
        labelKey: 'machineIdentity.operatingSystem.kernelRelease'
      },
      {
        field: 'machineIdentity.operatingSystem.kernelVersion',
        labelKey: 'machineIdentity.operatingSystem.kernelVersion'
      }
    ]
  },
  {
    sectionName: 'Agent',
    fields: [
      {
        field: 'machineIdentity.id',
        labelKey: 'machineIdentity.agent.agentId'
      },
      {
        field: 'machineIdentity.agent.installTime',
        labelKey: 'machineIdentity.agent.installTime',
        format: 'DATE'
      },
      {
        field: 'machineIdentity.agent.serviceStartTime',
        labelKey: 'machineIdentity.agent.serviceStartTime',
        format: 'DATE'
      },
      {
        field: 'machineIdentity.agent.serviceProcessId',
        labelKey: 'machineIdentity.agent.serviceProcessId'
      },
      {
        field: 'machineIdentity.agentMode',
        labelKey: 'machineIdentity.agent.agentMode'
      },
      {
        field: 'machineIdentity.agentVersion',
        labelKey: 'machineIdentity.agent.agentVersion'
      },
      {
        field: 'machineIdentity.agent.driverErrorCode',
        labelKey: 'machineIdentity.agent.driverErrorCode'
      }
    ]
  },

  {
    sectionName: 'Hardware',
    fields: [
      {
        field: 'machineIdentity.hardware.processorArchitecture',
        labelKey: 'machineIdentity.hardware.processorArchitecture'
      },
      {
        field: 'machineIdentity.hardware.processorArchitectureBits',
        labelKey: 'machineIdentity.hardware.processorArchitectureBits'
      },
      {
        field: 'machineIdentity.hardware.processorCount',
        labelKey: 'machineIdentity.hardware.processorCount'
      },
      {
        field: 'machineIdentity.hardware.totalPhysicalMemory',
        labelKey: 'machineIdentity.hardware.totalPhysicalMemory',
        format: 'SIZE'
      },
      {
        field: 'machineIdentity.hardware.manufacturer',
        labelKey: 'machineIdentity.hardware.manufacturer'
      },
      {
        field: 'machineIdentity.hardware.serial',
        labelKey: 'machineIdentity.hardware.serial'
      }
    ]
  },

  {
    sectionName: 'Locale',
    prefix: 'machineIdentity.locale',
    fields: [
      {
        field: 'machineIdentity.locale.isoCountryCode',
        labelKey: 'machineIdentity.locale.isoCountryCode'
      },
      {
        field: 'machineIdentity.locale.timeZone',
        labelKey: 'machineIdentity.locale.timeZone'
      }
    ]
  }
];
