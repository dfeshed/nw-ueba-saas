import { module, test } from 'qunit';
import { hostDetails } from '../../../state/state';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';

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
  selectedSnapshot,
  channelFiltersConfig,
  showWindowsLogPolicy,
  mftDownloadButtonStatusDetails,
  getRARStatus,
  hostOverviewServerId,
  isAgentMigrated,
  isolationStatus,
  manualFileDownloadVisualStatus,
  policyAdminUsm } from 'investigate-hosts/reducers/details/overview/selectors';

let setState;

module('Unit | Selectors | overview', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    revertPatch();
  });

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

  // This appears to be broken in the latest Chrome
  // It does not pass locally on Chrome 76, and with update to latest Chrome in
  // CI, this fails there too
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
    assert.equal(result[0].value, 'LUA');
    assert.equal(result[1].value, 'UAC');
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

  test('policyAdminUsm', function(assert) {
    const policyData = {
      serviceId: '63de7bb3-fcd3-415a-97bf-7639958cd5e6',
      serviceName: 'Node-X - Endpoint Server',
      usmRevision: 0,
      groups: [],
      policy: {
        edrPolicy: {
          name: 'Default EDR Policy',
          customConfig: '',
          transportConfig: {
            primary: {
              address: '10.40.15.154',
              httpsPort: 443,
              httpsBeaconIntervalInSeconds: 900,
              udpPort: 444,
              udpBeaconIntervalInSeconds: 30
            }
          },
          agentMode: 'ADVANCED',
          scheduledScanConfig: {
            enabled: true,
            recurrentSchedule: {
              recurrence: {
                interval: 1,
                unit: 'DAYS'
              },
              runAtTime: '09:00:00',
              runOnDaysOfWeek: [1],
              scheduleStartDate: '2019-03-22'
            },
            scanOptions: {
              cpuMax: 25,
              cpuMaxVm: 10,
              scanMbr: false }
          },
          fileDownloadConfig: {
            criteria: 'Unsigned',
            maxSize: 10000,
            enabled: true
          },
          blockingConfig: {
            enabled: false
          },
          isolationConfig: {
            enabled: true
          },
          storageConfig: {
            diskCacheSizeInMb: 100
          },
          serverConfig: {
            requestScanOnRegistration: false
          }
        },
        windowsLogPolicy: {
          enabled: true,
          primaryDestination: 'foo',
          secondaryDestination: 'Moo',
          protocol: '123',
          sendTestLog: true,
          channelFilters: [{ asd: 'asd' }],
          customConfig: ''
        },
        filePolicy: {
          name: 'Test File Policy',
          enabled: false,
          sendTestLog: true,
          primaryDestination: '111',
          secondaryDestination: '222',
          protocol: 'TLS',
          customConfig: '',
          sources: []
        }
      },
      policyStatus: 'Testing',
      evaluatedTime: '2019-05-07T05:25:41.109+0000'
    };
    const state = new ReduxDataHelper(setState).policy(policyData).build();
    const resultPolicyAdminUsm = policyAdminUsm(Immutable.from(state));
    assert.equal(resultPolicyAdminUsm.general.evaluatedTime, '2019-05-07T05:25:41.109+0000', 'evaluatedTime is correct');
    assert.deepEqual(resultPolicyAdminUsm.edrPolicy, {
      'agentMode': 'Advanced',
      'blockingEnabled': 'Disabled',
      'isolationEnabled': 'Enabled',
      'fileDownloadCriteria': 'Unsigned',
      'fileDownloadEnabled': 'Enabled',
      'maxFileDownloadSize': 10000,
      'maxFileDownloadSizeUnit': undefined,
      'cpuMax': '25 %',
      'cpuMaxVm': '10 %',
      'customConfig': '',
      'name': 'Default EDR Policy',
      'offlineDiskStorageSizeInMb': 100,
      'primaryAddress': '10.40.15.154',
      'primaryHttpsBeaconInterval': 15,
      'primaryHttpsBeaconIntervalUnit': 'MINUTES',
      'primaryHttpsPort': 443,
      'primaryUdpBeaconInterval': 30,
      'primaryUdpBeaconIntervalUnit': 'SECONDS',
      'primaryUdpPort': 444,
      'rarPolicyBeaconInterval': '',
      'rarPolicyPort': undefined,
      'rarPolicyServer': undefined,
      'recurrenceInterval': 1,
      'recurrenceUnit': 'DAYS',
      'requestScanOnRegistration': 'Disabled',
      'runOnDaysOfWeek': [
        'MONDAY'
      ],
      'scanMbr': 'Disabled',
      'scanStartDate': '2019-03-22',
      'scanStartTime': '09:00',
      'scanType': 'Scheduled'
    });
    assert.deepEqual(resultPolicyAdminUsm.windowsLogPolicy, {
      'channelFilters': [
        {
          'asd': 'asd'
        }
      ],
      'enabled': 'Enabled',
      'primaryDestination': 'foo',
      'protocol': '123',
      'secondaryDestination': 'Moo',
      'sendTestLog': 'Enabled',
      'customConfig': ''
    });
    assert.deepEqual(resultPolicyAdminUsm.filePolicy, {
      'sources': [],
      'enabled': false,
      'primaryDestination': '111',
      'protocol': 'TLS',
      'secondaryDestination': '222',
      'sendTestLog': true,
      'customConfig': ''
    });
    assert.deepEqual(resultPolicyAdminUsm.sources, {
      'hasWindowsLogPolicy': true,
      'hasFilePolicy': true
    });
  });

  test('policyAdminUsm when file download config not present', function(assert) {
    const policyData = {
      serviceId: '63de7bb3-fcd3-415a-97bf-7639958cd5e6',
      serviceName: 'Node-X - Endpoint Server',
      usmRevision: 0,
      groups: [],
      policy: {
        edrPolicy: {
          name: 'Default EDR Policy',
          customConfig: '',
          transportConfig: {
            primary: {
              address: '10.40.15.154',
              httpsPort: 443,
              httpsBeaconIntervalInSeconds: 900,
              udpPort: 444,
              udpBeaconIntervalInSeconds: 30
            }
          },
          agentMode: 'ADVANCED',
          scheduledScanConfig: {
            enabled: true,
            recurrentSchedule: {
              recurrence: {
                interval: 1,
                unit: 'DAYS'
              },
              runAtTime: '09:00:00',
              runOnDaysOfWeek: [1],
              scheduleStartDate: '2019-03-22'
            },
            scanOptions: {
              cpuMax: 25,
              cpuMaxVm: 10,
              scanMbr: false
            }
          },
          blockingConfig: {
            enabled: false
          },
          isolationConfig: {
            enabled: true
          },
          storageConfig: {
            diskCacheSizeInMb: 100
          },
          serverConfig: {
            requestScanOnRegistration: false
          }
        },
        windowsLogPolicy: {
          enabled: true,
          primaryDestination: 'foo',
          secondaryDestination: 'Moo',
          protocol: '123',
          sendTestLog: true,
          channelFilters: [{ asd: 'asd' }],
          customConfig: ''
        },
        filePolicy: {
          name: 'Test File Policy',
          enabled: false,
          sendTestLog: true,
          primaryDestination: '111',
          secondaryDestination: '222',
          protocol: 'TLS',
          customConfig: '',
          sources: []
        }
      },
      policyStatus: 'Testing',
      evaluatedTime: '2019-05-07T05:25:41.109+0000'
    };
    const state = new ReduxDataHelper(setState).policy(policyData).build();
    const resultPolicyAdminUsm = policyAdminUsm(Immutable.from(state));

    assert.deepEqual(resultPolicyAdminUsm.edrPolicy, {
      'agentMode': 'Advanced',
      'blockingEnabled': 'Disabled',
      'isolationEnabled': 'Enabled',
      'fileDownloadCriteria': undefined,
      'fileDownloadEnabled': 'Disabled',
      'maxFileDownloadSize': undefined,
      'maxFileDownloadSizeUnit': undefined,
      'cpuMax': '25 %',
      'cpuMaxVm': '10 %',
      'customConfig': '',
      'name': 'Default EDR Policy',
      'offlineDiskStorageSizeInMb': 100,
      'primaryAddress': '10.40.15.154',
      'primaryHttpsBeaconInterval': 15,
      'primaryHttpsBeaconIntervalUnit': 'MINUTES',
      'primaryHttpsPort': 443,
      'primaryUdpBeaconInterval': 30,
      'primaryUdpBeaconIntervalUnit': 'SECONDS',
      'primaryUdpPort': 444,
      'rarPolicyBeaconInterval': '',
      'rarPolicyPort': undefined,
      'rarPolicyServer': undefined,
      'recurrenceInterval': 1,
      'recurrenceUnit': 'DAYS',
      'requestScanOnRegistration': 'Disabled',
      'runOnDaysOfWeek': [
        'MONDAY'
      ],
      'scanMbr': 'Disabled',
      'scanStartDate': '2019-03-22',
      'scanStartTime': '09:00',
      'scanType': 'Scheduled'
    });
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
                    'udpPort': 7052,
                    'rar': {
                      'esh': 'esh',
                      'servers': [{ 'address': '10.40.15.116', 'httpsPort': 443, 'httpsBeaconIntervalInSeconds': 900 }]
                    }
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
                'isolationConfig': {
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
          'udpPort': 7052,
          'rar': {
            'config': {
              'address': '10.40.15.116',
              'httpsBeaconInterval': '15 minutes',
              'httpsBeaconIntervalInSeconds': 900,
              'httpsPort': 443
            },
            'esh': 'esh',
            'servers': [{ 'address': '10.40.15.116', 'httpsPort': 443, 'httpsBeaconIntervalInSeconds': 900 }]
          }
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
      'isolationConfig': {
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
      'isolationConfig': {
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
      'isolationConfig': {
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

    const state5 = {
      endpoint: {
        overview: {
          hostOverview: {},
          policyDetails: {
            policy: {
              edrPolicy: {
                enabled: true
              }
            }
          }
        }
      }
    };
    const result5 = getPoliciesPropertyData(Immutable.from(state5));
    assert.deepEqual(result5.windowsLogPolicy, undefined);

    const state6 = {
      endpoint: {
        overview: {
          hostOverview: {}
        }
      }
    };
    const result6 = getPoliciesPropertyData(Immutable.from(state6));
    assert.deepEqual(result6, {});
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

  test('showWindowsLogPolicy', function(assert) {
    const result1 = showWindowsLogPolicy(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {}
          },
          policyDetails: {
            policy: {
              windowsLogPolicy: {
                'enabled': true,
                'sendTestLog': false,
                'protocol': 'TLS',
                'channelFilters': [{
                  'eventId': 'ALL',
                  'filterType': 'include',
                  'channel': 'system'
                }]
              }
            }
          }
        }
      }
    }));
    assert.equal(result1, true);
    const result2 = showWindowsLogPolicy(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {}
          },
          policyDetails: {}
        }
      }
    }));
    assert.equal(result2, false);
  });

  test('channelFiltersConfig', function(assert) {
    const result1 = channelFiltersConfig(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {}
          },
          policyDetails: {
            policy: {
              windowsLogPolicy: {
                'enabled': true,
                'sendTestLog': false,
                'protocol': 'TLS',
                'channelFilters': [{
                  'eventId': 'ALL',
                  'filterType': 'include',
                  'channel': 'system'
                }]
              }
            }
          }
        }
      }
    }));
    assert.deepEqual(result1.fields, [{
      labelKey: 'system include',
      field: 'ALL',
      isStandardString: true
    }]);
  });

  test('hostOverviewServerId', function(assert) {
    const result1 = hostOverviewServerId(Immutable.from({ endpoint: { overview: {} } }));
    assert.deepEqual(result1, undefined);

    const result2 = hostOverviewServerId(Immutable.from({ endpoint: { overview: { hostOverview: { serviceId: '123' } } } }));
    assert.deepEqual(result2, '123');
  });

  test('mftDownloadButtonStatusDetails when true', function(assert) {
    const result = mftDownloadButtonStatusDetails(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            agentStatus: {
              isolationStatus: {
                isolated: true
              },
              lastSeen: 'EndpointServer'
            },
            machineIdentity: {
              machineOsType: 'windows',
              agentMode: 'advanced',
              agentVersion: '11.4.0.0'
            }
          }
        }
      }
    }));
    assert.deepEqual(result, { isDisplayed: true });
  });

  test('mftDownloadButtonStatusDetails when agentVersion is wrong', function(assert) {
    const result = mftDownloadButtonStatusDetails(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            agentStatus: {
              isolationStatus: {
                isolated: false
              }
            },
            machineIdentity: {
              machineOsType: 'windows',
              agentMode: 'advanced',
              agentVersion: '11.3.0.0'
            }
          }
        }
      }
    }));
    assert.deepEqual(result, { isDisplayed: false });
  });

  test('mftDownloadButtonStatusDetails when machineOsType is wrong', function(assert) {
    const result = mftDownloadButtonStatusDetails(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            agentStatus: {
              isolationStatus: {
                isolated: false
              }
            },
            machineIdentity: {
              machineOsType: 'mac',
              agentMode: 'advanced',
              agentVersion: '11.4.0.0'
            }
          }
        }
      }
    }));
    assert.deepEqual(result, { isDisplayed: false });
  });

  test('mftDownloadButtonStatusDetails when agentMode is wrong', function(assert) {
    const result = mftDownloadButtonStatusDetails(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            agentStatus: {
              isolationStatus: {
                isolated: false
              }
            },
            machineIdentity: {
              machineOsType: 'windows',
              agentMode: 'insights',
              agentVersion: '11.4.0.0'
            }
          }
        }
      }
    }));
    assert.deepEqual(result, { isDisplayed: false });
  });
  test('mftDownloadButtonStatusDetails when lastseen RelayServer', function(assert) {
    const result = mftDownloadButtonStatusDetails(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            agentStatus: {
              lastSeen: 'RelayServer',
              isolationStatus: {
                isolated: false
              }
            },
            machineIdentity: {
              machineOsType: 'windows',
              agentMode: 'insights',
              agentVersion: '11.4.0.0'
            }
          }
        }
      }
    }));
    assert.deepEqual(result, { isDisplayed: false });
  });
  test('mftDownloadButtonStatusDetails when isolation is true', function(assert) {
    const result = mftDownloadButtonStatusDetails(Immutable.from({
      endpoint: {
        overview: {
          hostOverview: {
            agentStatus: {
              isolationStatus: {
                isolated: true
              }
            },
            machineIdentity: {
              machineOsType: 'windows',
              agentMode: 'insights',
              agentVersion: '11.4.0.0'
            }
          }
        }
      }
    }));
    assert.deepEqual(result, { isDisplayed: false });
  });

  test('isolationStatus', function(assert) {
    const state1 = {
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {
              policyStatus: 'Updated'
            },
            machineIdentity: { machineOsType: 'windows', agentMode: 'advanced', agentVersion: '11.4.0.0' },
            agentStatus: { isolationStatus: { isolated: true } }
          },
          policyDetails: {
            evaluatedTime: '2018-11-19T09:16:35.969+0000',
            message: 'error message',
            policy: {
              'edrPolicy': {
                'agentMode': 'INSIGHTS',
                'isolationConfig': {
                  'enabled': false
                }
              }
            }
          }
        }
      }
    };
    const result1 = isolationStatus(Immutable.from(state1));
    assert.deepEqual(result1, { 'isIsolated': true, 'isIsolationEnabled': false });

    const state2 = {
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {
              policyStatus: 'Updated'
            },
            machineIdentity: { machineOsType: 'mac', agentMode: 'advanced', agentVersion: '11.4.0.0' },
            agentStatus: { isolationStatus: { } }
          },
          policyDetails: {
            evaluatedTime: '2018-11-19T09:16:35.969+0000',
            message: 'error message',
            policy: {
              'edrPolicy': {
                'agentMode': 'INSIGHTS',
                'isolationConfig': {
                  'enabled': false
                }
              }
            }
          }
        }
      }
    };
    const result2 = isolationStatus(Immutable.from(state2));
    assert.deepEqual(result2, { 'isIsolated': false, 'isIsolationEnabled': false });

    const state3 = {
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {
              policyStatus: 'Updated'
            },
            machineIdentity: { machineOsType: 'windows', agentMode: 'advanced', agentVersion: '11.4.0.0' },
            agentStatus: { isolationStatus: { } }
          },
          policyDetails: {
            evaluatedTime: '2018-11-19T09:16:35.969+0000',
            message: 'error message',
            policy: {
              'edrPolicy': {
                'agentMode': 'INSIGHTS',
                'isolationConfig': {
                  'enabled': true
                }
              }
            }
          }
        }
      }
    };
    const result3 = isolationStatus(Immutable.from(state3));
    assert.deepEqual(result3, { 'isIsolated': false, 'isIsolationEnabled': true });
  });

  test('getRARStatus', function(assert) {
    const result1 = getRARStatus(Immutable.from({ endpoint: { overview: { hostDetails: { agentStatus: { lastSeen: 'RelayServer' } } } } }));
    assert.equal(result1, true, 'Is a roaming agent');
    const result2 = getRARStatus(Immutable.from({ endpoint: { overview: { hostDetails: { agentStatus: { lastSeen: 'EndpointServer' } } } } }));
    assert.equal(result2, false, 'Is not a roaming agent');
  });

  test('isAgentMigrated is not broker', function(assert) {
    const state = Immutable.from({
      endpoint: {
        overview: {
          hostDetails: {
            groupPolicy: {
              managed: false
            }
          }
        },
        selectedHostList: [{ id: 1, version: '4.4', managed: false }]
      },
      endpointServer: {
        serviceData: [{ id: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0', name: 'endpoint-server' },
          { id: 'f9be528a-ca5b-463b-bc3f-deab7cc36bb9', name: 'endpoint-broker-server' }]
      },
      endpointQuery: {
        serverId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
      }
    });
    const result = isAgentMigrated(state);
    assert.equal(result, true);
  });

  test('isAgentMigrated is broker', function(assert) {
    const state = Immutable.from({
      endpoint: {
        overview: {
          hostDetails: {
            groupPolicy: {
              managed: true
            }
          }
        },
        selectedHostList: [{ id: 1, version: '4.4', managed: false }]
      },
      endpointServer: {
        serviceData: [{ id: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0', name: 'endpoint-broker-server' },
          { id: 'f9be528a-ca5b-463b-bc3f-deab7cc36bb9', name: 'endpoint-server' }]
      },
      endpointQuery: {
        serverId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
      }
    });
    const result = isAgentMigrated(state);
    assert.equal(result, false);
  });

  test('manualFileDownloadVisualStatus', function(assert) {
    const state1 = {
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {
              policyStatus: 'Updated'
            },
            machineIdentity: { machineOsType: 'windows', agentMode: 'advanced', agentVersion: '11.4.0.0' },
            agentStatus: { isolationStatus: { isolated: true } }
          }
        }
      }
    };

    const result1 = manualFileDownloadVisualStatus(state1);
    assert.equal(result1.isDisplayed, false, 'Returns false as the agent version does not match');

    const state2 = {
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {
              policyStatus: 'Updated'
            },
            machineIdentity: { machineOsType: 'windows', agentMode: 'insight', agentVersion: '11.5.0.0' },
            agentStatus: { isolationStatus: { isolated: true } }
          }
        }
      }
    };

    const result2 = manualFileDownloadVisualStatus(state2);
    assert.equal(result2.isDisplayed, false, 'Returns false as the agent mode does not match');

    const state3 = {
      endpoint: {
        overview: {
          hostOverview: {
            groupPolicy: {
              policyStatus: 'Updated'
            },
            machineIdentity: { machineOsType: 'windows', agentMode: 'advanced', agentVersion: '11.5.0.0' },
            agentStatus: { isolationStatus: { isolated: true } }
          }
        }
      }
    };

    const result3 = manualFileDownloadVisualStatus(state3);
    assert.equal(result3.isDisplayed, true, 'Returns true as the agent version and mode match');
  });
});
