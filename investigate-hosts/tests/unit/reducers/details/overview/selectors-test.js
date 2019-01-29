import { module, test } from 'qunit';
import { hostDetails } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | overview');

import {
  machineOsType,
  isJsonExportCompleted,
  getSecurityConfigurations,
  sameConfigStatus,
  arrangedSecurityConfigs,
  isEcatAgent,
  downloadLink,
  hostWithStatus,
  isMachineWindows,
  getPoliciesPropertyData,
  selectedSnapshot } from 'investigate-hosts/reducers/details/overview/selectors';

test('machineOsType', function(assert) {
  const result = machineOsType(Immutable.from({ endpoint: { overview: { hostOverview: { machineIdentity: { machineOsType: 'linux' } } } } }));
  assert.equal(result, 'linux');
});

test('isJsonExportCompleted', function(assert) {
  const result = isJsonExportCompleted(Immutable.from({ endpoint: { overview: { exportJSONStatus: 'completed' } } }));
  assert.equal(result, true);
});

test('getSecurityConfigurations', function(assert) {
  const result = getSecurityConfigurations(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.length, 2);
});

test('getSecurityConfigurations when machine is undefined', function(assert) {
  const result = getSecurityConfigurations(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: {
          id: 'aswe1'
        }
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('getSecurityConfigurations when security Configuration is undefined', function(assert) {
  const result = getSecurityConfigurations(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: {
          machine: {},
          machineIdentity: {}
        }
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('arrangedSecurityConfigs when arrangeBy is status', function(assert) {
  const result = arrangedSecurityConfigs(Immutable.from({
    endpoint: {
      overview: {
        arrangeSecurityConfigsBy: 'status',
        hostDetails: {
          machine: {
            securityConfigurations: [
              'uacDisabled',
              'luaDisabled'
            ]
          },
          machineIdentity: {
            machineOsType: 'windows'
          }
        }
      }
    }
  }));
  assert.equal(result[0].value, 'UAC');
  assert.equal(result[1].value, 'LUA');
});

test('sameConfigStatus check for same config status for all the security configs', function(assert) {
  const result = sameConfigStatus(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: {
          machine: {
            securityConfigurations: [
              'uacDisabled',
              'luaDisabled'
            ]
          },
          machineIdentity: {
            machineOsType: 'windows'
          }
        }
      }
    }
  }));

  assert.deepEqual(result, false);
});

test('isEcatAgent to check the agent is 4.4 agent', function(assert) {
  const result = isEcatAgent(Immutable.from({
    endpoint: {
      overview: {
        hostOverview: {
          machine: {},
          machineIdentity: {
            machineOsType: 'windows',
            agentVersion: '4.4.0.2'
          }
        }
      }
    }
  }));

  assert.deepEqual(result, true);
});

test('isEcatAgent to check the agent is not 4.4 agent', function(assert) {
  const result = isEcatAgent(Immutable.from({
    endpoint: {
      overview: {
        hostOverview: {
          machine: {},
          machineIdentity: {
            machineOsType: 'windows',
            agentVersion: '4.1.0.1'
          }
        }
      }
    }
  }));

  assert.deepEqual(result, false);
});

test('isEcatAgent, when hostOverview is null', function(assert) {
  const result = isEcatAgent(Immutable.from({
    endpoint: {
      overview: {
        hostOverview: null
      }
    }
  }));

  assert.deepEqual(result, false);
});

test('isEcatAgent, when hostOverview.machineIdentity is null', function(assert) {
  const result = isEcatAgent(Immutable.from({
    endpoint: {
      overview: {
        hostOverview: {
          machineIdentity: null
        }
      }
    }
  }));

  assert.deepEqual(result, false);
});

// isMachineWindows
test('isMachineWindows, if the OS of the selected agent is not Windows', function(assert) {
  const result = isMachineWindows(Immutable.from({ endpoint: { overview: { hostOverview: { machineIdentity: { machineOsType: 'linux' } } } } }));
  assert.equal(result, false, 'OS of the Selected agent is not Windows, value is false');
});

test('isMachineWindows, if the OS of the selected agent is Windows', function(assert) {
  const result = isMachineWindows(Immutable.from({ endpoint: { overview: { hostOverview: { machineIdentity: { machineOsType: 'windows' } } } } }));
  assert.equal(result, true, 'OS of the Selected agent is Windows, value is true');
});

test('downloadLink', function(assert) {
  const result = downloadLink(Immutable.from({
    endpoint: {
      overview: {
        downloadId: '123'
      }
    },
    endpointQuery: {}
  }));

  assert.deepEqual(result, null);
});

test('downloadLink with serverId', function(assert) {
  const result1 = downloadLink(Immutable.from({
    endpoint: {
      overview: {
        downloadId: '123'
      }
    },
    endpointQuery: {
      serverId: '234'
    }
  }));

  assert.ok(result1.includes('/rsa/endpoint/234/machine/download?id=123'));

  const result2 = downloadLink(Immutable.from({
    endpoint: {
      overview: {
        downloadId: '123'
      }
    },
    endpointQuery: {
      selectedMachineServerId: '2456'
    }
  }));

  assert.ok(result2.includes('/rsa/endpoint/2456/machine/download?id=123'));
});

test('hostWithStatus', function(assert) {
  let result = hostWithStatus(Immutable.from({
    endpoint: {
      overview: {
        hostOverview: {
          id: 1,
          agentStatus: {
            agentId: 1,
            status: 'idle'
          }
        }
      },
      machines: {
        agentStatus: {
          agentId: 2
        }
      }
    }
  }));

  assert.equal(result.agentStatus.status, 'idle');

  result = hostWithStatus(Immutable.from({
    endpoint: {
      overview: {
        hostOverview: {
          id: 1,
          agentStatus: {
            agentId: 1,
            status: 'idle'
          }
        }
      },
      machines: {
        agentStatus: {
          agentId: 1,
          status: 'scanning'
        }
      }
    }
  }));

  assert.equal(result.agentStatus.status, 'scanning');
});

test('getPoliciesPropertyData', function(assert) {
  const state1 = {
    endpoint: {
      overview: {
        hostOverview: {
          groupPolicy: {
            policyStatus: 'Updated'
          }
        },
        policyDetails: {
          evaluatedTime: '2018-11-19T09:16:35.969+0000',
          message: 'error message',
          policy: {
            'edrPolicy': {
              'agentMode': 'INSIGHTS',
              'transportConfig': {
                'primary': {
                  'httpsBeaconIntervalInSeconds': 910,
                  'udpBeaconIntervalInSeconds': 30,
                  'address': '10.40.12.8',
                  'httpsPort': 7050,
                  'udpPort': 7052
                }
              },
              'scheduledScanConfig': {
                'enabled': true,
                'scanOptions': {
                  'cpuMax': 80,
                  'cpuMaxVm': 100,
                  'scanMbr': false
                }
              },
              'blockingConfig': {
                'enabled': false
              },
              'serverConfig': {
                'requestScanOnRegistration': false
              }
            }
          }
        }
      }
    }
  };

  const result1 = getPoliciesPropertyData(Immutable.from(state1));

  assert.equal(result1.policyStatus, 'Updated');
  assert.equal(result1.evaluatedTime, '2018-11-19T09:16:35.969+0000');
  assert.equal(result1.message, 'error message');
  assert.deepEqual(result1.edrPolicy, {
    'agentMode': 'Insights',
    'transportConfig': {
      'primary': {
        'httpsBeaconInterval': '15 minutes 10 seconds',
        'udpBeaconInterval': '30 seconds',
        'httpsBeaconIntervalInSeconds': 910,
        'udpBeaconIntervalInSeconds': 30,
        'address': '10.40.12.8',
        'httpsPort': 7050,
        'udpPort': 7052
      }
    },
    'scheduledScanConfig': {
      'enabled': 'Scheduled',
      'scanOptions': {
        'cpuMax': '80 %',
        'cpuMaxVm': '100 %',
        'scanMbr': 'Disabled'
      }
    },
    'blockingConfig': {
      'enabled': 'Disabled'
    },
    'serverConfig': {
      'requestScanOnRegistration': 'Disabled'
    }
  });

  const state2 = {
    endpoint: {
      overview: {
        hostOverview: {
          groupPolicy: {}
        },
        policyDetails: {
          policy: {
            'edrPolicy': {
              'agentMode': 'FULL_MONITORING',
              'scheduledScanConfig': {
                'enabled': true,
                'scanOptions': {
                  'cpuMax': 80,
                  'cpuMaxVm': 100,
                  'scanMbr': false
                }
              }
            }
          }
        }
      }
    }
  };

  const result2 = getPoliciesPropertyData(Immutable.from(state2));

  assert.deepEqual(result2.edrPolicy, {
    'agentMode': 'Advanced',
    'blockingConfig': {
      'enabled': 'Disabled'
    },
    'scheduledScanConfig': {
      'enabled': 'Scheduled',
      'scanOptions': {
        'cpuMax': '80 %',
        'cpuMaxVm': '100 %',
        'scanMbr': 'Disabled'
      }
    },
    'serverConfig': {
      'requestScanOnRegistration': 'Disabled'
    },
    'transportConfig': {}
  });

  const state3 = {
    endpoint: {
      overview: {
        hostOverview: {
          groupPolicy: {}
        },
        policyDetails: {
          policy: {
            'edrPolicy': {
              'agentMode': 'INSIGHTS',
              'scheduledScanConfig': {}
            }
          }
        }
      }
    }
  };

  const result3 = getPoliciesPropertyData(Immutable.from(state3));

  assert.deepEqual(result3.edrPolicy, {
    'agentMode': 'Insights',
    'blockingConfig': {
      'enabled': 'Disabled'
    },
    'scheduledScanConfig': {
      'enabled': 'Manual'
    },
    'serverConfig': {
      'requestScanOnRegistration': 'Disabled'
    },
    'transportConfig': {}
  });

  const state4 = {
    endpoint: {
      overview: {
        hostOverview: {
          groupPolicy: {}
        },
        policyDetails: {
          policy: {
            'windowsLogPolicy': {
              'enabled': true,
              'sendTestLog': false,
              'protocol': 'TLS'
            }
          }
        }
      }
    }
  };

  const result4 = getPoliciesPropertyData(Immutable.from(state4));

  assert.deepEqual(result4.windowsLogPolicy, {
    'enabled': 'Enabled',
    'sendTestLog': 'Disabled',
    'protocol': 'TLS'
  });
});

test('selectedSnapshot', function(assert) {
  const result1 = selectedSnapshot(Immutable.from({
    endpoint: {
      detailsInput: {
        snapShots: [
          {
            serviceId: '123',
            scanStartTime: '2019-01-23T09:23:12:422Z'
          },
          {
            serviceId: '13',
            scanStartTime: '2019-01-24T09:23:12:422Z'
          }
        ],
        scanTime: '2019-01-23T09:23:12:422Z',
        agentId: 'awe2312'
      }
    }
  }));
  assert.deepEqual(result1, {
    serviceId: '123',
    scanStartTime: '2019-01-23T09:23:12:422Z'
  });
  const result2 = selectedSnapshot(Immutable.from({
    endpoint: {
      detailsInput: {
        snapShots: null,
        scanTime: '2019-01-23T09:25:12:422Z',
        agentId: 'awe2312'
      }
    }
  }));
  assert.deepEqual(result2, null);
});