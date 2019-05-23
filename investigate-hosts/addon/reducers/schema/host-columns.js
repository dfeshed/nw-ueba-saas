const HOST_LIST_COLUMNS = [
  {
    name: 'agentStatus.lastSeenTime',
    dataType: 'DATE'
  },
  {
    name: 'agentStatus.scanStatus',
    dataType: 'STRING'
  },
  {
    name: 'groupPolicy.groups.name',
    description: 'Agent Group',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.networkInterfaces.ipv4',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'groupPolicy.policyStatus',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.agentMode',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'machine.scanStartTime',
    description: 'Last Scan Time',
    dataType: 'DATE'
  },
  {
    name: 'machine.users.name',
    description: 'User Name',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.agentVersion',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.agent.driverErrorCode',
    dataType: 'LONG'
  },
  {
    name: 'machineIdentity.machineOsType',
    description: 'Operating System',
    dataType: 'STRING',
    values: [
      'windows',
      'linux',
      'mac'
    ]
  },
  {
    name: 'machineIdentity.operatingSystem.description',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'id',
    description: 'Agent Id',
    dataType: 'STRING'
  },
  {
    name: 'machine.scanTrigger',
    dataType: 'STRING'
  },
  {
    name: 'machine.securityConfigurations',
    description: '',
    dataType: 'STRING',
    values: [
      'allowAccessDataSourceDomain',
      'allowDisplayMixedContent',
      'antiVirusDisabled',
      'badCertificateWarningDisabled',
      'cookiesCleanupDisabled',
      'crossSiteScriptFilterDisabled',
      'firewallDisabled',
      'fileVaultDisabled',
      'gatekeeperDisabled',
      'ieDepDisabled',
      'ieEnhancedSecurityDisabled',
      'intranetZoneNotificationDisabled',
      'kextSigningDisabled',
      'luaDisabled',
      'windowsUpdateDisabled',
      'registryToolsDisabled',
      'safariFraudWebsiteWarningDisabled',
      'smartScreenFilterDisabled',
      'sudoersNoPasswordPrompt',
      'systemIntegrityProtectionDisabled',
      'systemRestoreDisabled',
      'taskManagerDisabled',
      'uacDisabled',
      'warningPostRedirectionDisabled',
      'warningOnZoneCrossingDisabled'
    ]
  },
  {
    name: 'machineIdentity.networkInterfaces.name',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.networkInterfaces.macAddress',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.networkInterfaces.ipv6',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.networkInterfaces.promiscuous',
    dataType: 'BOOLEAN'
  },
  {
    name: 'machine.users.sessionType',
    dataType: 'STRING'
  },
  {
    name: 'machine.users.isAdministrator',
    dataType: 'BOOLEAN'
  },
  {
    name: 'machine.users.domainUserQualifiedName',
    dataType: 'STRING'
  },
  {
    name: 'machine.users.domainUserId',
    dataType: 'STRING'
  },
  {
    name: 'machine.securityProducts.displayName',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.machineName',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.agent.exeCompileTime',
    dataType: 'DATE'
  },
  {
    name: 'machineIdentity.agent.packageTime',
    dataType: 'DATE'
  },
  {
    name: 'machineIdentity.agent.installTime',
    dataType: 'DATE'
  },
  {
    name: 'machineIdentity.agent.serviceStartTime',
    dataType: 'DATE'
  },
  {
    name: 'machineIdentity.agent.serviceStatus',
    description: '',
    dataType: 'STRING',
    values: [
      'ntfsLowLevelReads',
      'ntfsRawReads',
      'ntfsPhysicalDrive',
      'ntfsPartitionDrive',
      'httpsFallbackMode'
    ]
  },
  {
    name: 'machineIdentity.operatingSystem.buildNumber',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.servicePack',
    dataType: 'INT'
  },
  {
    name: 'machineIdentity.operatingSystem.directory',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.kernelId',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.kernelName',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.kernelRelease',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.kernelVersion',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.distribution',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.domainComputerId',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.domainComputerOu',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.domainComputerCanonicalOu',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.domainOrWorkgroup',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.domainRole',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.operatingSystem.lastBootTime',
    dataType: 'DATE'
  },
  {
    name: 'machineIdentity.hardware.processorArchitecture',
    description: '',
    dataType: 'STRING',
    values: [
      'x86',
      'x64'
    ]
  },
  {
    name: 'machineIdentity.hardware.processorArchitectureBits',
    dataType: 'INT'
  },
  {
    name: 'machineIdentity.hardware.processorCount',
    dataType: 'INT'
  },
  {
    name: 'machineIdentity.hardware.processorName',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.hardware.totalPhysicalMemory',
    dataType: 'LONG'
  },
  {
    name: 'machineIdentity.hardware.chassisType',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.hardware.manufacturer',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.hardware.model',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.hardware.serial',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.hardware.bios',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.locale.isoCountryCode',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'machineIdentity.locale.timeZone',
    dataType: 'STRING'
  },
  {
    name: 'groupPolicy.serverName',
    dataType: 'STRING'
  },
  {
    name: 'score',
    dataType: 'INT'
  }
];
export default HOST_LIST_COLUMNS;
