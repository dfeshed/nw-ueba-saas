/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [
  {
    'propertyName': 'machine.agentVersion',
    'label': 'investigateHosts.hosts.column.machine.agentVersion',
    'filterControl': 'host-list/content-filter/text-filter',
    'panelId': 'agentVersion',
    'selected': true,
    'isDefault': false,
    'filterType': 'agentVersion',
    'invalidError': 'invalidAgentVersion'
  },
  {
    'propertyName': 'id',
    'label': 'investigateHosts.hosts.column.machine.id',
    'filterControl': 'host-list/content-filter/text-filter',
    'panelId': 'id',
    'isDefault': false,
    'selected': true,
    'showMemUnit': true,
    'filterType': 'agentID',
    'invalidError': 'invalidAgentID'
  },
  {
    'propertyName': 'machine.machineOsType',
    'label': 'investigateHosts.hosts.column.machine.machineOsType',
    'filterControl': 'host-list/content-filter/list-filter',
    'selected': false,
    'panelId': 'machineOsType',
    'isDefault': false

  },
  {
    'propertyName': 'machine.machineName',
    'label': 'investigateHosts.hosts.column.machine.machineName',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'machineName',
    'isDefault': false,
    'isValidate': false
  },
  {
    'propertyName': 'machine.users.name',
    'label': 'investigateHosts.hosts.column.machine.users.name',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'uname',
    'isDefault': false,
    'isValidate': false
  },
  {
    'propertyName': 'machine.scanStartTime',
    'label': 'investigateHosts.hosts.column.machine.scanStartTime',
    'options': [
      { label: '5 Minute', id: 'LAST_FIVE_MINUTES', selected: true, value: 5, unit: 'Minutes' },
      { label: '10 Minutes', id: 'LAST_TEN_MINUTES', value: 10, unit: 'Minutes' },
      { label: '15 Minutes', id: 'LAST_FIFTEEN_MINUTES', value: 15, unit: 'Minutes' },
      { label: '30 Minutes', id: 'LAST_THIRTY_MINUTES', value: 30, unit: 'Minutes' },
      { label: '1 Hour', id: 'LAST_ONE_HOUR', value: 1, unit: 'Hours' },
      { label: '3 Hours', id: 'LAST_THREE_HOURS', value: 3, unit: 'Hours' },
      { label: '6 Hours', id: 'LAST_SIX_HOURS', value: 6, unit: 'Hours' },
      { label: '12 Hours', id: 'LAST_TWELVE_HOURS', value: 12, unit: 'Hours' },
      { label: '24 Hours', id: 'LAST_TWENTY_FOUR_HOURS', value: 24, unit: 'Hours' },
      { label: '2 Days', id: 'LAST_TWO_DAYS', value: 2, unit: 'Days' },
      { label: '5 Days', id: 'LAST_FIVE_DAYS', value: 5, unit: 'Days' }
    ],
    'filterControl': 'host-list/content-filter/datetime-filter',
    'showDateRange': true,
    'selected': false,
    'panelId': 'scanStartTime',
    'isDefault': false,
    'showRadioButtons': true
  },
  {
    'propertyName': 'machine.securityConfigurations',
    'label': 'investigateHosts.hosts.column.machine.securityConfigurations',
    'filterControl': 'host-list/content-filter/list-filter',
    'selected': false,
    'panelId': 'securityConfigurations',
    'isDefault': false
  },
  {
    'propertyName': 'machine.networkInterfaces.macAddress',
    'label': 'investigateHosts.hosts.column.machine.networkInterfaces.macAddress',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'macAddress',
    'isDefault': false,
    'filterType': 'macAddress',
    'invalidError': 'invalidMacAddress'
  },
  {
    'propertyName': 'machine.networkInterfaces.promiscuous',
    'label': 'investigateHosts.hosts.column.machine.networkInterfaces.promiscuous',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'promiscuous',
    'isDefault': false
  },
  {
    'propertyName': 'machine.users.domainUserOu',
    'label': 'investigateHosts.hosts.column.machine.users.domainUserOu',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'domainUserOu',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.group',
    'label': 'investigateHosts.hosts.column.machineIdentity.group',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'group',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.agent.serviceStatus',
    'label': 'investigateHosts.hosts.column.machineIdentity.agent.serviceStatus',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'serviceStatus',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.agent.driverStatus',
    'label': 'investigateHosts.hosts.column.machineIdentity.agent.driverStatus',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'driverStatus',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.agent.blockingEnabled',
    'label': 'investigateHosts.hosts.column.machineIdentity.agent.blockingEnabled',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'agentBlockingEnabled',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.operatingSystem.description',
    'label': 'investigateHosts.hosts.column.machineIdentity.operatingSystem.description',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'description',
    'isDefault': false,
    'filterType': 'osDescription',
    'invalidError': 'invalidOsDescription'
  },
  {
    'propertyName': 'machineIdentity.operatingSystem.kernelVersion',
    'label': 'investigateHosts.hosts.column.machineIdentity.operatingSystem.kernelVersion',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'kernelVersion',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.hardware.processorArchitecture',
    'label': 'investigateHosts.hosts.column.machineIdentity.hardware.processorArchitecture',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'processorArchitecture',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.locale.isoCountryCode',
    'label': 'investigateHosts.hosts.column.machineIdentity.locale.isoCountryCode',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'isoCountryCode',
    'isDefault': false,
    'filterType': 'countryCode',
    'invalidError': 'invalidCountryCode'
  },
  {
    'propertyName': 'machineIdentity.agentMode',
    'label': 'investigateHosts.hosts.column.machineIdentity.agentMode',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'agentMode',
    'isDefault': false,
    'filterType': 'onlyAlphabetChars',
    'invalidError': 'invalidCharsAlphabetOnly'
  },
  {
    'propertyName': 'analysisData.machineRiskScore',
    'label': 'investigateHosts.hosts.column.analysisData.machineRiskScore',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'machineRiskScore',
    'isDefault': false
  },
  {
    'propertyName': 'analysisData.iocs',
    'label': 'investigateHosts.hosts.column.analysisData.iocs',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'iocs',
    'isDefault': false
  },
  {
    'propertyName': 'agentStatus.lastSeenTime',
    'label': 'investigateHosts.hosts.filters.agentStatus.lastSeenTime',
    'options': [
      { label: '1 Hour', id: 'LAST_ONE_HOUR', selected: true, value: 1, unit: 'Hours' },
      { label: '3 Hours', id: 'LAST_THREE_HOURS', value: 3, unit: 'Hours' },
      { label: '6 Hours', id: 'LAST_SIX_HOURS', value: 6, unit: 'Hours' },
      { label: '12 Hours', id: 'LAST_TWELVE_HOURS', value: 12, unit: 'Hours' },
      { label: '24 Hours', id: 'LAST_TWENTY_FOUR_HOURS', value: 24, unit: 'Hours' },
      { label: '2 Days', id: 'LAST_TWO_DAYS', value: 2, unit: 'Days' },
      { label: '5 Days', id: 'LAST_FIVE_DAYS', value: 5, unit: 'Days' }
    ],
    'filterControl': 'host-list/content-filter/datetime-filter',
    'showDateRange': false,
    'selected': false,
    'panelId': 'lastSeenTime',
    'isDefault': false,
    'showRadioButtons': false,
    'restrictionTypeForRelativeTime': 'LESS_THAN'
  },
  {
    'propertyName': 'agentStatus.scanStatus',
    'label': 'investigateHosts.hosts.column.agentStatus.scanStatus',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'scanStatus',
    'isDefault': false
  },
  {
    'propertyName': 'machine.networkInterfaces.ipv4',
    'label': 'investigateHosts.hosts.column.machine.networkInterfaces.ipv4',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'ipv4Status',
    'isDefault': false,
    'filterType': 'ip',
    'invalidError': 'invalidIP'
  }

];

export {
  FILTER_TYPES
};
