/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [
  {
    'name': 'score',
    'label': 'investigateHosts.hosts.column.score',
    'step': 1,
    'type': 'range'
  },
  {
    'name': 'machineIdentity.machineOsType',
    'label': 'investigateHosts.hosts.column.machineIdentity.machineOsType',
    'listOptions': [
      { name: 'windows', label: 'investigateFiles.filter.fileType.pe' },
      { name: 'linux', label: 'investigateFiles.filter.fileType.linux' },
      { name: 'mac', label: 'investigateFiles.filter.fileType.macho' }
    ],
    type: 'list'
  },
  {
    'name': 'machineIdentity.agentMode',
    'label': 'investigateHosts.hosts.column.machineIdentity.agentMode',
    'listOptions': [
      { name: 'insights', label: 'investigateHosts.hosts.filters.agentMode.insights' },
      { name: 'advanced', label: 'investigateHosts.hosts.filters.agentMode.advanced' }
    ],
    type: 'list'
  },
  {
    'name': 'groupPolicy.groups.name',
    'label': 'investigateHosts.hosts.column.groupPolicy.groups.name',
    'type': 'text',
    'validations': {
      format: {
        validator: (value) => {
          return !(/^([!-~\s])*$/.test(value));
        },
        message: 'investigateHosts.hosts.filters.invalidGroup'
      }
    }
  },
  {
    name: 'agentStatus.lastSeenTime',
    label: 'investigateHosts.hosts.column.agentStatus.lastSeenTime',
    type: 'date',
    showCustomDate: false,
    timeframes: [
      { name: 'LAST_ONE_HOUR', selected: true, value: 1, unit: 'Hours' },
      { name: 'LAST_THREE_HOURS', value: 3, unit: 'Hours' },
      { name: 'LAST_SIX_HOURS', value: 6, unit: 'Hours' },
      { name: 'LAST_TWELVE_HOURS', value: 12, unit: 'Hours' },
      { name: 'LAST_TWENTY_FOUR_HOURS', value: 24, unit: 'Hours' },
      { name: 'LAST_TWO_DAYS', value: 2, unit: 'Days' },
      { name: 'LAST_SEVEN_DAYS', value: 7, unit: 'Days' },
      { name: 'LAST_TWO_WEEKS', value: 14, unit: 'Days' },
      { name: 'LAST_ONE_MONTH', value: 30, unit: 'Days' }
    ]
  },
  {
    name: 'machine.scanStartTime',
    label: 'investigateHosts.hosts.column.machine.scanStartTime',
    type: 'date',
    timeframes: [
      { name: 'LAST_FIVE_MINUTES', value: 5, unit: 'Minutes' },
      { name: 'LAST_TEN_MINUTES', value: 10, unit: 'Minutes' },
      { name: 'LAST_FIFTEEN_MINUTES', value: 15, unit: 'Minutes' },
      { name: 'LAST_THIRTY_MINUTES', value: 30, unit: 'Minutes' },
      { name: 'LAST_ONE_HOUR', value: 1, unit: 'Hours' },
      { name: 'LAST_THREE_HOURS', value: 3, unit: 'Hours' },
      { name: 'LAST_SIX_HOURS', value: 6, unit: 'Hours' },
      { name: 'LAST_TWELVE_HOURS', value: 12, unit: 'Hours' },
      { name: 'LAST_TWENTY_FOUR_HOURS', value: 24, unit: 'Hours' },
      { name: 'LAST_TWO_DAYS', value: 2, unit: 'Days' },
      { name: 'LAST_SEVEN_DAYS', value: 7, unit: 'Days' }
    ]
  },
  {
    'name': 'machineIdentity.machineName',
    'label': 'investigateHosts.hosts.column.machineIdentity.machineName',
    'type': 'text'
  },
  {
    'name': 'machine.users.name',
    'label': 'investigateHosts.hosts.column.machine.users.name',
    'type': 'text'
  },
  {
    'name': 'machineIdentity.agent.driverErrorCode',
    'label': 'investigateHosts.hosts.column.machineIdentity.agent.driverErrorCode',
    'type': 'text'
  },
  {
    'name': 'machineIdentity.networkInterfaces.macAddress',
    'label': 'investigateHosts.hosts.column.machineIdentity.networkInterfaces.macAddress',
    'type': 'text',
    'validations': {
      format: {
        exclude: ['LIKE'],
        validator: (value) => {
          return !(/^(?:[0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$/.test(value));
        },
        message: 'investigateHosts.hosts.filters.invalidMacAddress'
      }
    },
    'placeholder': 'e.g.,00:00:00:00:00:00'
  },
  {
    'name': 'machineIdentity.networkInterfaces.ipv4',
    'label': 'investigateHosts.hosts.column.machineIdentity.networkInterfaces.ipv4',
    'type': 'text',
    'validations': {
      format: {
        exclude: ['LIKE'],
        validator: (value) => {
          const ips = value.split('||');
          const isValidIps = ips.map((ip) => {
            return (/^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(ip.trim()));
          });
          return isValidIps.includes(false);
        },
        message: 'investigateHosts.hosts.filters.invalidIP'
      }
    },
    'placeholder': 'e.g., 0.0.0.0'
  },
  {
    'name': 'machine.securityConfigurations',
    'label': 'investigateHosts.hosts.column.machine.securityConfigurations',
    multiSelect: true,
    'listOptions': [
      { name: 'allowAccessDataSourceDomain', label: 'investigateHosts.hosts.filters.securityConfig.allowAccessDataSourceDomain' },
      { name: 'allowDisplayMixedContent', label: 'investigateHosts.hosts.filters.securityConfig.allowDisplayMixedContent' },
      { name: 'antiVirusDisabled', label: 'investigateHosts.hosts.filters.securityConfig.antiVirusDisabled' },
      { name: 'badCertificateWarningDisabled', label: 'investigateHosts.hosts.filters.securityConfig.badCertificateWarningDisabled' },
      { name: 'cookiesCleanupDisabled', label: 'investigateHosts.hosts.filters.securityConfig.cookiesCleanupDisabled' },
      { name: 'crossSiteScriptFilterDisabled', label: 'investigateHosts.hosts.filters.securityConfig.crossSiteScriptFilterDisabled' },
      { name: 'firewallDisabled', label: 'investigateHosts.hosts.filters.securityConfig.firewallDisabled' },
      { name: 'fileVaultDisabled', label: 'investigateHosts.hosts.filters.securityConfig.fileVaultDisabled' },
      { name: 'gatekeeperDisabled', label: 'investigateHosts.hosts.filters.securityConfig.gatekeeperDisabled' },
      { name: 'ieDepDisabled', label: 'investigateHosts.hosts.filters.securityConfig.ieDepDisabled' },
      { name: 'ieEnhancedSecurityDisabled', label: 'investigateHosts.hosts.filters.securityConfig.ieEnhancedSecurityDisabled' },
      { name: 'intranetZoneNotificationDisabled', label: 'investigateHosts.hosts.filters.securityConfig.intranetZoneNotificationDisabled' },
      { name: 'kextSigningDisabled', label: 'investigateHosts.hosts.filters.securityConfig.kextSigningDisabled' },
      { name: 'luaDisabled', label: 'investigateHosts.hosts.filters.securityConfig.luaDisabled' },
      { name: 'windowsUpdateDisabled', label: 'investigateHosts.hosts.filters.securityConfig.windowsUpdateDisabled' },
      { name: 'registryToolsDisabled', label: 'investigateHosts.hosts.filters.securityConfig.registryToolsDisabled' },
      { name: 'safariFraudWebsiteWarningDisabled', label: 'investigateHosts.hosts.filters.securityConfig.safariFraudWebsiteWarningDisabled' },
      { name: 'smartScreenFilterDisabled', label: 'investigateHosts.hosts.filters.securityConfig.smartScreenFilterDisabled' },
      { name: 'sudoersNoPasswordPrompt', label: 'investigateHosts.hosts.filters.securityConfig.sudoersNoPasswordPrompt' },
      { name: 'systemIntegrityProtectionDisabled', label: 'investigateHosts.hosts.filters.securityConfig.systemIntegrityProtectionDisabled' },
      { name: 'systemRestoreDisabled', label: 'investigateHosts.hosts.filters.securityConfig.systemRestoreDisabled' },
      { name: 'taskManagerDisabled', label: 'investigateHosts.hosts.filters.securityConfig.taskManagerDisabled' },
      { name: 'uacDisabled', label: 'investigateHosts.hosts.filters.securityConfig.uacDisabled' },
      { name: 'warningPostRedirectionDisabled', label: 'investigateHosts.hosts.filters.securityConfig.warningPostRedirectionDisabled' },
      { name: 'warningOnZoneCrossingDisabled', label: 'investigateHosts.hosts.filters.securityConfig.warningOnZoneCrossingDisabled' }
    ],
    type: 'dropdown'
  },
  {
    'name': 'id',
    'label': 'investigateHosts.hosts.column.machine.id',
    'type': 'text',
    'validations': {
      format: {
        validator: (value) => {
          return !(/^[A-Za-z0-9-]*$/.test(value));
        },
        message: 'investigateHosts.hosts.filters.invalidAgentID'
      }
    }
  },
  {
    'name': 'machineIdentity.agentVersion',
    'label': 'investigateHosts.hosts.column.machineIdentity.agentVersion',
    'type': 'text',
    'placeholder': 'e.g., 11.0.0',
    'validations': {
      format: {
        validator: (value) => {
          return !(/^[0-9.]*$/.test(value));
        },
        message: 'investigateHosts.hosts.filters.invalidAgentVersion'
      }
    }
  }
];

export {
  FILTER_TYPES
};
