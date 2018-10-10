/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [

  {
    'name': 'machine.agentVersion',
    'label': 'investigateHosts.hosts.column.machine.agentVersion',
    'type': 'text',
    'validations': {
      format: {
        validator: (value) => {
          return !(/^[0-9.]*$/.test(value));
        },
        message: 'investigateHosts.hosts.filters.invalidAgentVersion'
      }
    }
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
    'name': 'machine.machineOsType',
    'label': 'investigateHosts.hosts.column.machine.machineOsType',
    'listOptions': [
      { name: 'windows', label: 'investigateFiles.filter.fileType.pe' },
      { name: 'linux', label: 'investigateFiles.filter.fileType.linux' },
      { name: 'mac', label: 'investigateFiles.filter.fileType.macho' }
    ],
    type: 'list'
  },

  {
    'name': 'machine.machineName',
    'label': 'investigateHosts.hosts.column.machine.machineName',
    'type': 'text'
  },
  {
    'name': 'machine.users.name',
    'label': 'investigateHosts.hosts.column.machine.users.name',
    'type': 'text'
  },
  // TODO : ADD securityConfigurations

  // TODO : ADD scan time filter

  {
    'name': 'machine.networkInterfaces.macAddress',
    'label': 'investigateHosts.hosts.column.machine.networkInterfaces.macAddress',
    'type': 'text',
    'validations': {
      format: {
        exclude: ['LIKE'],
        validator: (value) => {
          return !(/^(?:[0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$/.test(value));
        },
        message: 'investigateHosts.hosts.filters.invalidMacAddress'
      }
    }
  },
  {
    'name': 'machineIdentity.locale.isoCountryCode',
    'label': 'investigateHosts.hosts.column.machineIdentity.locale.isoCountryCode',
    'type': 'text',
    'validations': {
      format: {
        validator: (value) => {
          return !(/^[A-Za-z_]*$/.test(value));
        },
        message: 'investigateHosts.hosts.filters.invalidCountryCode'
      }
    }
  },
  // TODO : ADD agentmode
  // TODO ADD risk score
  // TODO LAST SEEN time
  // SCAN STATUS
  {
    'name': 'machine.networkInterfaces.ipv4',
    'label': 'investigateHosts.hosts.column.machine.networkInterfaces.ipv4',
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
    }
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
  }
];

export {
  FILTER_TYPES
};
