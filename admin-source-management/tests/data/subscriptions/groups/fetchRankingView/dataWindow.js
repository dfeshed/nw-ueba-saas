export default
{
  'policy': {
    'id': '__default_windows_log_policy',
    'name': 'Default Windows Log Policy',
    'description': 'These are the settings that are applied when not defined in another policy applied to an agent.',
    'policyType': 'windowsLogPolicy',
    'defaultPolicy': true,
    'enabled': false,
    'primaryDestination': '',
    'secondaryDestination': '',
    'protocol': 'TLS',
    'sendTestLog': false,
    'lastPublishedOn': 1514764800000,
    'dirty': false,
    'channelFilters': [
      {
        'channel': 'Security',
        'eventId': '620,630,640',
        'filterType': 'EXCLUDE'
      }
    ]
  },
  'origins': {
    'id': {
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
    'policyType': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'defaultPolicy': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'enabled': {
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
    'sendTestLog': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'lastPublishedOn': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'dirty': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'channelFilters': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    }
  }
};
