/* eslint-env node */

module.exports = [
  {
    'createdBy': 'admin',
    'createdOn': 1495517131585,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1495517131585,
    'id': '5923c7cbd8d4ae128db98c98',
    'name': 'JAZZ_NWE_5_AGENTS',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.agentVersion',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': '5.0.0.0'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'createdBy': 'admin',
    'createdOn': 1495517157609,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1495517157609,
    'id': '5923c7e5d8d4ae128db98c99',
    'name': 'windows_linux_mac_agents',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'windows'
            },
            {
              'value': 'linux'
            },
            {
              'value': 'mac'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'createdBy': 'admin',
    'createdOn': 1495517202472,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1495517202472,
    'id': '5923c812d8d4ae128db98c9a',
    'name': 'Server_Machine_Names',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineName',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': 'Server'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'createdBy': 'admin',
    'createdOn': 1495517247256,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1495517247256,
    'id': '5923c83fd8d4ae128db98c9b',
    'name': 'x64_Machines',
    'filterType': 'Machine',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machineIdentity.hardware.processorArchitecture',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'x64'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'createdBy': 'admin',
    'createdOn': 1495517347594,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1495533544404,
    'id': '5923c8a3d8d4ae128db98c9d',
    'name': 'ecat_username',
    'description': '',
    'filterType': 'Machine',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.users.name',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'ecat'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'createdBy': 'admin',
    'createdOn': 1495538406859,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1495538406859,
    'id': '59241ae6d8d4ae128db98c9e',
    'name': 'Security_Config_allow_access_data_source_domain_filter',
    'filterType': 'Machine',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.securityConfigurations',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'allowAccessDataSourceDomain'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'createdBy': 'admin',
    'createdOn': 1495538453080,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1495538460177,
    'id': '59241b15d8d4ae128db98c9f',
    'name': 'Security_configuration_firewall_disabled',
    'filterType': 'Machine',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'windows'
            }
          ]
        },
        {
          'propertyName': 'machine.securityConfigurations',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'firewallDisabled'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'createdOn': 0,
    'lastModifiedOn': 0,
    'id': 'linux',
    'name': 'Linux',
    'filterType': 'Machine',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'linux'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': true
  },
  {
    'createdOn': 0,
    'lastModifiedOn': 0,
    'id': 'windows',
    'name': 'Windows',
    'filterType': 'Machine',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'windows'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': true
  },
  {
    'createdOn': 0,
    'lastModifiedOn': 0,
    'id': 'all',
    'name': 'Machines by Top Risk Score',
    'filterType': 'Machine',
    'systemFilter': true
  },
  {
    'createdOn': 0,
    'lastModifiedOn': 0,
    'id': 'mac',
    'name': 'Mac',
    'filterType': 'Machine',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'mac'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': true
  }
];