export default [
  {
    'id': 'group_001',
    'name': 'Zebra 001',
    'description': 'Zebra 001 of group group_001',
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337,
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
    'createdBy': 'local',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655368173,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        [
          'ipv4',
          'BETWEEN',
          [
            '123',
            '22'
          ]
        ]
      ]
    }
  },
  {
    'id': 'group_003',
    'name': 'Xylaphone 003',
    'description': 'Xylaphone 003 of group group_003',
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_004',
    'name': 'Wonder Woman 004',
    'description': 'Wonder Woman 004 of group group_004',
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337,
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
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_006',
    'name': 'Ummm... 006',
    'description': 'Umm... 006 of group group_006',
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_007',
    'name': 'Football 007',
    'description': 'Football 007 of group group_007',
    'createdBy': 'local',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_008',
    'name': 'Excellent! 008',
    'description': 'Excellent! 008 of group group_008',
    'createdBy': 'local',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_009',
    'name': 'Dog Food 009',
    'description': 'Dog Food 009 of group group_009',
    'createdBy': 'local',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_010',
    'name': 'Cat Woman 010',
    'description': 'Cat Woman 010 of group group_010',
    'createdBy': 'local',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_011',
    'name': 'Basketball 011',
    'description': 'Basketball 011 of group group_011',
    'createdBy': 'local',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_002',
        'name': 'policy_02'
      }
    },
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_012',
    'name': 'Yabba Dabba Doo! 002',
    'description': 'Yabba Dabba Doo! 002 of group group_002',
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337,
    'osTypes': [],
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_001',
        'name': 'EDR Policy 001'
      }
    },
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_013',
    'name': 'Tom n Jerry 013',
    'description': 'Tom n Jerry 013 of group group_013',
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'dirty': false,
    'lastPublishedCopy': null,
    'lastPublishedOn': 1523655354337,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  },
  {
    'id': 'group_014',
    'name': 'Garfield 014',
    'description': 'Garfield 014 of group group_014',
    'createdBy': 'local',
    'createdOn': 1523655368173,
    'dirty': true,
    'lastPublishedCopy': null,
    'lastPublishedOn': 0,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655368173,
    'assignedPolicies': {},
    'groupCriteria': {
      'conjunction': 'AND',
      'criteria': [
        ['osType', 'IN', []]
      ]
    }
  }

];
