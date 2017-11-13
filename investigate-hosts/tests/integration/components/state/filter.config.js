/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const filterConfig = [
  {
    'propertyName': 'machine.agentVersion',
    'label': 'investigateHosts.hosts.column.machine.agentVersion',
    'filterControl': 'host-list/content-filter/text-filter',
    'panelId': 'agentVersion',
    'selected': true,
    'isDefault': true
  }
];

export default filterConfig;