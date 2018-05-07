/* eslint-env node */
module.exports = [
  {
    'id': '5abb6028a93cb02e8ca05d0e',
    'name': 'ecat agents',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.agentVersion',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': '4.4',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5abc9574a93cb01e89665e9c',
    'name': 'Pooja Agent',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineName',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': 'NDKM',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5ac2587ca93cb026bf4db462',
    'name': 'linuxMac215113',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'linux',
              'relative': false
            },
            {
              'value': 'mac',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5ac258daa93cb026bf4db463',
    'name': 'windowsLinux215247',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'windows',
              'relative': false
            },
            {
              'value': 'linux',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5ad996eda93cb060d48602ee',
    'name': '11_2_agents',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.agentVersion',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': '11.2',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5ad99765a93cb060d48609c9',
    'name': 'test_events',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.networkInterfaces.ipv4',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': '10.40.5.110',
              'relative': false
            },
            {
              'value': '10.40.5.120',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5add5affa93cb060d4862d6f',
    'name': '1040756_DNU',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.networkInterfaces.ipv4',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': '56',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5add6b92a93cb060d4863988',
    'name': 'Swati',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.networkInterfaces.ipv4',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': '10.40.7.34',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5ae6e5f4a93cb0207bbb4123',
    'name': '11-2-testing-10-4-5-110',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'id',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': 'AC4345EA-0DB9-C02C-C7D5-3D87F2BEDD3C',
              'relative': false
            }
          ]
        },
        {
          'propertyName': 'machine.agentVersion',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': '11.2',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5ae9868ca93cb0207bbb8942',
    'name': '238',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.networkInterfaces.ipv4',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': '238',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5aebdddda93cb0207bbbe3cd',
    'name': 'testing',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.networkInterfaces.ipv4',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': '10.40.5.115',
              'relative': false
            },
            {
              'value': '10.40.7.56',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5aec30e7a93cb0207bbbfa02',
    'name': 'test1',
    'description': '',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'id',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': '1',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': 'linux',
    'name': 'Linux',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'linux',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': true
  },
  {
    'id': 'windows',
    'name': 'Windows',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'windows',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': true
  },
  {
    'id': 'mac',
    'name': 'Mac',
    'filterType': 'MACHINE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'machine.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'mac',
              'relative': false
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': true
  }];