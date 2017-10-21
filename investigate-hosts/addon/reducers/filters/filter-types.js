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
    'isDefault': true
  },
  {
    'propertyName': 'id',
    'label': 'investigateHosts.hosts.column.machine.id',
    'filterControl': 'host-list/content-filter/text-filter',
    'panelId': 'id',
    'isDefault': true,
    'selected': true,
    'showMemUnit': true
  },
  {
    'propertyName': 'machine.machineOsType',
    'label': 'investigateHosts.hosts.column.machine.machineOsType',
    'filterControl': 'host-list/content-filter/text-filter',
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
    'isDefault': false
  },
  {
    'propertyName': 'machine.users.name',
    'label': 'investigateHosts.hosts.column.machine.users.name',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'uname',
    'isDefault': false
  },
  {
    'propertyName': 'machine.scanStartTime',
    'label': 'investigateHosts.hosts.column.machine.scanStartTime',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'scanStartTime',
    'isDefault': false
  },
  {
    'propertyName': 'machine.securityConfigurations',
    'label': 'investigateHosts.hosts.column.machine.securityConfigurations',
    'filterControl': 'host-list/content-filter/text-filter',
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
    'isDefault': false
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
    'propertyName': 'machineIdentity.agent.driverErrorCode',
    'label': 'investigateHosts.hosts.column.machineIdentity.agent.driverErrorCode',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'driverErrorCode',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.agent.blockingEnabled',
    'label': 'investigateHosts.hosts.column.machineIdentity.agent.blockingEnabled',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'agent.blockingEnabled',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.operatingSystem.description',
    'label': 'investigateHosts.hosts.column.machineIdentity.operatingSystem.description',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'description',
    'isDefault': false
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
    'panelId': '.processorArchitecture',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.locale.isoCountryCode',
    'label': 'investigateHosts.hosts.column.machineIdentity.locale.isoCountryCode',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'isoCountryCode',
    'isDefault': false
  },
  {
    'propertyName': 'machineIdentity.agentMode',
    'label': 'investigateHosts.hosts.column.machineIdentity.agentMode',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'agentMode',
    'isDefault': false
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
    'label': 'investigateHosts.hosts.column.agentStatus.lastSeenTime',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'lastSeenTime',
    'isDefault': false
  },
  {
    'propertyName': 'agentStatus.scanStatus',
    'label': 'investigateHosts.hosts.column.agentStatus.scanStatus',
    'filterControl': 'host-list/content-filter/text-filter',
    'selected': false,
    'panelId': 'scanStatus',
    'isDefault': false
  }

];

export {
  FILTER_TYPES
};
