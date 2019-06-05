export default [
  {
    'id': 'group_001',
    'name': 'Zebra 001',
    'description': 'Zebra 001 of group group_001',
    'createdBy': 'admin',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655354337,
    'sourceCount': -1,
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_001',
        'name': 'EMC 001'
      }
    },
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        [
          'osType',
          'IN',
          [
            'Linux'
          ]
        ]
      ]
    }
  },
  {
    id: 'group_002',
    'name': 'Awesome! 012',
    'description': 'Awesome! 012 of group group_012',
    'createdBy': 'admin',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655368173,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655368173,
    'sourceCount': 30,
    'assignedPolicies': {},
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        [
          'ipv4',
          'BETWEEN',
          [
            '1.2.3.4',
            '5.6.7.8'
          ]
        ]
      ]
    }
  },
  {
    'id': 'group_003',
    'name': 'Xylaphone 003',
    'description': 'Xylaphone 003 of group group_003',
    'createdBy': 'admin',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'sourceCount': -2,
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', ['Linux']]
      ]
    }
  },
  {
    'id': 'group_004',
    'name': 'Wonder Woman 004',
    'description': 'Wonder Woman 004 of group group_004',
    'createdBy': 'admin',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655354337,
    'sourceCount': 250,
    'assignedPolicies': {},
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        [
          'osType',
          'IN',
          [
            'Windows'
          ]
        ],
        [
          'osType',
          'IN',
          [
            'Windows'
          ]
        ],
        [
          'osDescription',
          'ENDS_WITH',
          [
            'hebjc'
          ]
        ],
        [
          'ipv4',
          'NOT_IN',
          [
            '125.1.1.227,125.1.1.78\n'
          ]
        ],
        [
          'hostname',
          'EQUAL',
          [
            'trbkx'
          ]
        ],
        [
          'osDescription',
          'CONTAINS',
          [
            'xltbk'
          ]
        ],
        [
          'ipv4',
          'NOT_BETWEEN',
          [
            '1.1.1.45',
            '1.1.2.193'
          ]
        ]
      ]
    }
  },
  {
    'id': 'group_005',
    'name': 'Volleyball 005',
    'description': 'Volleyball 005 of group group_005',
    'createdBy': 'admin',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'sourceCount': -2,
    'groupCriteria': {
      'conjunction': 'OR',
      'criteria': [
        [
          'ipv4',
          'BETWEEN',
          [
            '10.40.14.0',
            '10.40.14.255'
          ]
        ],
        [
          'ipv4',
          'BETWEEN',
          [
            '10.40.68.0',
            '10.40.68.255'
          ]
        ]
      ]
    }
  },
  {
    'id': 'group_006',
    'name': 'Ummm... 006llllllllllllllllllllllllllllllllllllllllllllllllllllloooooooooooooooooooooooooooooooooooooooooooooooooooooooooooonnnnnnnnnnnnnnnnnnnnnnnggggggggggg',
    'description': 'Umm... 006 of group group_006llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllooooooooooooooooooooooooooooooooooooonnnnnnnnnnnnnnnnggggg',
    'createdBy': 'admin',
    'createdOn': 1523655354337,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'sourceCount': 10,
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', ['Linux']]
      ]
    }
  },
  {
    'id': 'group_007',
    'name': 'Football 007',
    'description': 'Football 007 of group group_007',
    'createdBy': 'admin',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'sourceCount': -3,
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        [
          'ipv4',
          'IN',
          [
            '10.40.14.101,10.40.14.108,10.40.14.123,10.40.14.171'
          ]
        ]
      ]
    }
  },
  {
    'id': 'group_008',
    'name': 'Excellent! 008',
    'description': 'Group using Default EDR Policy',
    'createdBy': 'admin',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': '__default_edr_policy',
        'name': 'Default EDR Policy'
      }
    },
    'sourceCount': -3,
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        [
          'ipv4',
          'BETWEEN',
          [
            '10.101.0.0',
            '10.5.101.255'
          ]
        ]
      ]
    }
  },
  {
    'id': 'group_009',
    'name': 'Dog Food 009',
    'description': 'Dog Food 009 of group group_009',
    'createdBy': 'admin',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'sourceCount': -3,
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', ['Linux']]
      ]
    }
  },
  {
    'id': 'group_010',
    'name': 'Cat Woman 010',
    'description': 'Cat Woman 010 of group group_010',
    'createdBy': 'admin',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'sourceCount': -3,
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', ['Linux']]
      ]
    }
  },
  {
    'id': 'group_011',
    'name': 'Basketball 011',
    'description': 'Basketball 011 of group group_011',
    'createdBy': 'admin',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655368173,
    'sourceCount': -3,
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_002',
        'name': 'policy_02'
      },
      'windowsLogPolicy': {
        'referenceId': 'policy_WL001',
        'name': 'WL001'
      },
      'filePolicy': {
        'referenceId': 'policy_F001',
        'name': 'F001'
      }
    },
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', ['Linux']]
      ]
    }
  },
  {
    'id': 'group_012',
    'name': 'Yabba Dabba Doo! 002',
    'description': 'Yabba Dabba Doo! 002 of group group_002',
    'createdBy': 'admin',
    'createdOn': 1523655354337,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655354337,
    'sourceCount': -3,
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_001',
        'name': 'EDR Policy 001'
      }
    },
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', ['Linux']]
      ]
    }
  },
  {
    'id': 'group_013',
    'name': 'Tom n Jerry 013',
    'description': 'Tom n Jerry 013 of group group_013',
    'createdBy': 'admin',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'sourceCount': 10,
    'groupCriteria': {
      'conjunction': 'OR',
      'criteria': [
        [
          'ipv4',
          'BETWEEN',
          [
            '10.40.68.0',
            '10.40.68.255'
          ]
        ],
        [
          'ipv4',
          'NOT_BETWEEN',
          [
            '10.40.200.0',
            '10.40.200.255'
          ]
        ]
      ]
    }
  },
  {
    'id': 'group_014',
    'name': 'Garfield 014',
    'description': 'Garfield 014 of group group_014',
    'createdBy': 'admin',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'admin',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'sourceCount': -3,
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        [
          'hostname',
          'IN',
          [
            'jaylpt, monilpt, johnlpt, viveklpt'
          ]
        ]
      ]
    }
  }
];