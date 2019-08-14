export default [
  // default file policy
  {
    'policy': {
      'id': '__default_file_policy',
      'policyType': 'filePolicy',
      'name': 'Default File Policy',
      'description': 'These are the settings that are applied when not defined in another policy applied to an agent.',
      'dirty': false,
      'defaultPolicy': true,
      'createdBy': 'admin',
      'createdOn': 0,
      'lastModifiedOn': 0,
      'lastModifiedBy': 'admin',
      'lastPublishedOn': 1514744800000,
      'enabled': false,
      'sendTestLog': false,
      'protocol': 'TLS'
      // 'sources': []
    },
    'origins': {
      'id': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'policyType': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'name': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'description': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'dirty': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'defaultPolicy': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'createdBy': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'createdOn': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'lastModifiedOn': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'lastModifiedBy': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'lastPublishedOn': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'enabled': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'sendTestLog': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'primaryDestination': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'secondaryDestination': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'protocol': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      }
      // 'sources': {
      //   'groupName': 'test',
      //   'policyName': 'test',
      //   'conflict': false
      // }
    }
  },
  // file policy with all settings
  {
    'policy': {
      'id': 'policy_F001',
      'policyType': 'filePolicy',
      'name': 'F001',
      'description': 'File Policy # F001',
      'dirty': false,
      'defaultPolicy': false,
      'createdBy': 'admin',
      'createdOn': 0,
      'lastModifiedOn': 0,
      'lastModifiedBy': 'admin',
      'lastPublishedOn': 1514744800000,
      'enabled': true,
      'sendTestLog': false,
      'primaryDestination': '10.10.10.10',
      'secondaryDestination': '10.10.10.12',
      'protocol': 'TLS',
      'sources': [
        {
          fileType: 'apache',
          enabled: false,
          startOfEvents: false,
          fileEncoding: 'UTF-8 / ASCII',
          paths: ['/c/apache_path-hint-1/*.log', '/c/Program Files/Apache Group/Apache[2-9]/*.log', 'apache_path-hint-2'],
          sourceName: 'Meta-Source-Name',
          exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3']
        },
        {
          fileType: 'exchange',
          enabled: true,
          startOfEvents: true,
          fileEncoding: 'UTF-8 / ASCII',
          paths: ['/[cd]/exchange/logs/*.log'],
          sourceName: 'Exchange aye!',
          exclusionFilters: []
        }
      ]
    },
    'origins': {
      'id': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'policyType': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'name': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'description': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'dirty': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'defaultPolicy': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'createdBy': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'createdOn': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'lastModifiedOn': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'lastModifiedBy': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'lastPublishedOn': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'enabled': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'sendTestLog': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'primaryDestination': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'secondaryDestination': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'protocol': {
        'groupName': 'test',
        'policyName': 'test',
        'conflict': false
      },
      'sources.apache': {
        'groupName': 'apache groupName',
        'policyName': 'apache policyName',
        'conflict': false
      },
      'sources.exchange': {
        'groupName': 'exchange groupName',
        'policyName': 'exchange policyName',
        'conflict': false
      }
    }
  }
];
