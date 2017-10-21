/* eslint-env node */
export default {
  'type': 'machine',
  'fields': [
    {
      'name': 'id',
      'description': 'Agent Id',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.agentVersion',
      'description': 'Agent Version',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.machineOsType',
      'description': 'Operating System',
      'dataType': 'STRING',
      'values': [
        'windows',
        'linux',
        'mac'
      ],
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.machineName',
      'description': 'Machine Name',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.scanStartTime',
      'description': 'Last Scan Time',
      'dataType': 'DATE',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.scanType',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.scanTrigger',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.securityConfigurations',
      'description': 'Security Configurations',
      'dataType': 'STRING',
      'values': [
        'allowAccessDataSourceDomain',
        'allowDisplayMixedContent',
        'antiVirusDisabled',
        'badCertificateWarningDisabled',
        'cookiesCleanupDisabled',
        'crossSiteScriptFilterDisabled',
        'firewallDisabled',
        'fileVaultDisabled',
        'gatekeeperDisabled',
        'iEDepDisabled',
        'iEEnhancedSecurityDisabled',
        'intranetZoneNotificationDisabled',
        'kextDevMode',
        'lUADisabled',
        'noAntivirusNotificationDisabled',
        'noFirewallNotificationDisabled',
        'noUACNotificationDisabled',
        'noWindowsUpdateDisabled',
        'registryToolsDisabled',
        'safariFraudWebsiteWarningDisabled',
        'smartScreenFilterDisabled',
        'sudoersNoPasswordPrompt',
        'systemIntegrityProtectionDisabled',
        'systemRestoreDisabled',
        'taskManagerDisabled',
        'uACDisabled',
        'warningPostRedirectionDisabled',
        'warningOnZoneCrossingDisabled'
      ],
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.name',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.macAddress',
      'description': 'NetworkInterface - Name',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.networkId',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.ipv4',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.ipv6',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.gateway',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.dns',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.networkInterfaces.promiscuous',
      'description': 'NetworkInterface - Promiscuous',
      'dataType': 'BOOLEAN',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'BOOLEAN'
    },
    {
      'name': 'machine.users.name',
      'description': 'User Name',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.sessionId',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'machine.users.sessionType',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.isAdministrator',
      'dataType': 'BOOLEAN',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'BOOLEAN'
    },
    {
      'name': 'machine.users.groups',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.domainUserQualifiedName',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.domainUserId',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.domainUserOu',
      'description': 'Users - Domain User OU',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.domainUserCanonicalOu',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.host',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.users.deviceName',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.hostFileEntries.ip',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machine.hostFileEntries.hosts',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.group',
      'description': '',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.description',
      'description': 'OS - Description',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.buildNumber',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.servicePack',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'machineIdentity.operatingSystem.directory',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.kernelId',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.kernelName',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.kernelRelease',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.kernelVersion',
      'description': 'OS - Kernel Version',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.distribution',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.domainComputerId',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.domainComputerOu',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.domainComputerCanonicalOu',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.domainOrWorkgroup',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.domainRole',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.operatingSystem.lastBootTime',
      'description': '',
      'dataType': 'DATE',
      'searchable': false,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.hardware.processorArchitecture',
      'description': '',
      'dataType': 'STRING',
      'values': [
        'x86',
        'x64'
      ],
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.hardware.processorArchitectureBits',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'machineIdentity.hardware.processorCount',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'machineIdentity.hardware.processorName',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.hardware.totalPhysicalMemory',
      'dataType': 'LONG',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'machineIdentity.hardware.chassisType',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.hardware.manufacturer',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.hardware.model',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.hardware.serial',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.hardware.bios',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.locale.defaultLanguage',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.locale.isoCountryCode',
      'description': '',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.locale.timeZone',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'machineIdentity.agentMode',
      'description': 'Agent Mode',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'analysisData.machineRiskScore',
      'description': 'Risk Score',
      'dataType': 'INT',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'analysisData.iocs',
      'description': 'IOC Alerts',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'agentStatus.lastSeenTime',
      'description': 'Last Seen Time',
      'dataType': 'DATE',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'agentStatus.scanStatus',
      'description': 'Agent Status',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    }
  ]
};