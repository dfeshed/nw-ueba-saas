import { module, test } from 'qunit';
import { hostDetails } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | overview');

import {
  machineOsType,
  isJsonExportCompleted,
  getNetworkInterfaces,
  getLoggedInUsers,
  getSecurityConfigurations,
  sameConfigStatus,
  arrangedSecurityConfigs,
  isEcatAgent,
  isMachineLinux } from 'investigate-hosts/reducers/details/overview/selectors';

test('machineOsType', function(assert) {
  const result = machineOsType(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result, 'linux');
});

test('isJsonExportCompleted', function(assert) {
  const result = isJsonExportCompleted(Immutable.from({ endpoint: { overview: { exportJSONStatus: 'completed' } } }));
  assert.equal(result, true);
});

test('getNetworkInferaces', function(assert) {
  const result = getNetworkInterfaces(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.length, 2, 'validIPList length');
  assert.deepEqual(result[1],
    {
      'ipv4': [
        '10.40.15.187',
        '10.40.12.7'
      ],
      'ipv6': [
        'fe80::250:56ff:fe01:2bb5',
        'fe80::250:56ff:fe01:4701'
      ],
      'macAddress': '00:50:56:01:2B:B5'
    });
});

test('getLoggedInUsers', function(assert) {
  const result = getLoggedInUsers(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.length, 2);
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
          machine: {}
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
            machineOsType: 'windows',
            securityConfigurations: [
              'uacDisabled',
              'luaDisabled'
            ]
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
            machineOsType: 'windows',
            securityConfigurations: [
              'uacDisabled',
              'luaDisabled'
            ]
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
        hostDetails: {
          machine: {
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
        hostDetails: {
          machine: {
            machineOsType: 'windows',
            agentVersion: '4.1.0.1'
          }
        }
      }
    }
  }));

  assert.deepEqual(result, false);
});

test('isEcatAgent, when hostDetails is null', function(assert) {
  const result = isEcatAgent(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: null
      }
    }
  }));

  assert.deepEqual(result, false);
});

test('isEcatAgent, when hostDetails.machine is null', function(assert) {
  const result = isEcatAgent(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: {
          machine: null
        }
      }
    }
  }));

  assert.deepEqual(result, false);
});

test('isMachineLinux, if the OS of the selected agent is undefined', function(assert) {
  const result = isMachineLinux(Immutable.from({ endpoint: { overview: { hostDetails: {} } } }));
  assert.equal(result, false, 'machineOsType of the Selected agent is undefined, vlaue is false');
});

test('isMachineLinux, if the OS of the selected agent is not Linux', function(assert) {
  const result = isMachineLinux(Immutable.from({ endpoint: { overview: { hostDetails: { machine: { machineOsType: 'windows' } } } } }));
  assert.equal(result, false, 'OS of the Selected agent is not Linux, value is false');
});

test('isMachineLinux, if the OS of the selected agent Linux', function(assert) {
  const result = isMachineLinux(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result, true, 'OS of the Selected agent is Linux, value is true');
});
