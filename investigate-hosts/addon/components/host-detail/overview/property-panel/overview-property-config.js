export default [
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
        field: 'machineIdentity.agent.serviceEprocess',
        labelKey: 'machineIdentity.agent.serviceEprocess'
      },
      {
        field: 'machineIdentity.agent.serviceProcessId',
        labelKey: 'machineIdentity.agent.serviceProcessId'
      },
      {
        field: 'machineIdentity.agent.serviceErrorCode',
        labelKey: 'machineIdentity.agent.serviceErrorCode'
      },
      {
        field: 'machineIdentity.agent.driverErrorCode',
        labelKey: 'machineIdentity.agent.driverErrorCode',
        format: 'HEX'
      },
      {
        field: 'machineIdentity.agentMode',
        labelKey: 'machineIdentity.agent.agentMode'
      },
      {
        field: 'machine.agentVersion',
        labelKey: 'machineIdentity.agent.agentVersion'
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
        labelKey: 'machineIdentity.hardware.totalPhysicalMemory'
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
    sectionName: 'Network Interfaces',
    prefix: 'machine.networkInterfaces',
    multiOption: true,
    fields: [
      {
        field: 'name',
        labelKey: 'machine.networkInterfaces.name'
      },
      {
        field: 'macAddress',
        labelKey: 'machine.networkInterfaces.macAddress'
      },
      {
        field: 'ipv4',
        labelKey: 'machine.networkInterfaces.ipv4'
      },
      {
        field: 'ipv6',
        labelKey: 'machine.networkInterfaces.ipv6'
      },
      {
        field: 'gateway',
        labelKey: 'machine.networkInterfaces.gateway'
      },
      {
        field: 'dns',
        labelKey: 'machine.networkInterfaces.dns'
      },
      {
        field: 'promiscuous',
        labelKey: 'machine.networkInterfaces.promiscuous'
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
        labelKey: 'machine.users.name'
      },
      {
        field: 'sessionId',
        labelKey: 'machine.users.sessionId'
      },
      {
        field: 'administrator',
        labelKey: 'machine.users.isAdministrator'
      }
    ]
  },
  {
    sectionName: 'Locale',
    prefix: 'machineIdentity.locale',
    fields: [
      {
        field: 'machineIdentity.locale.defaultLanguage',
        labelKey: 'machineIdentity.locale.defaultLanguage'
      },
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