import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  areSomeScanning,
  hostExportLink,
  processedHostList,
  isAllHostSelected,
  noHostsSelected,
  serviceList,
  allAreEcatAgents,
  hasEcatAgents,
  warningMessages,
  isScanStartButtonDisabled,
  isScanStopButtonDisabled,
  extractAgentIds,
  isExportButtonDisabled,
  hostListPropertyTabs,
  hostTotalLabel,
  nextLoadCount,
  isInsightsAgent,
  allAreMigratedHosts,
  mftDownloadButtonStatus,
  isAgentMigrated,
  actionsDisableMessage,
  isolationComment,
  excludedIps,
  selectedHostDetails,
  agentVersionSupported,
  agentVersionNotSupported } from 'investigate-hosts/reducers/hosts/selectors';

module('Unit | selectors | hosts');
const STATE = Immutable.from({
  endpoint: {
    filter: {},
    machines: {
      totalItems: 2,
      hostList: [
        {
          id: 1,
          agentStatus: {
            scanStatus: 'scaning',
            lastSeen: 'RelayServer'
          },
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '11.3.0.0'
          }
        },
        {
          id: 2,
          agentStatus: {
            scanStatus: 'idle',
            lastSeen: 'EndpointServer'
          },
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '11.4.0.0'
          }
        }
      ],
      hostExportLinkId: 123,
      selectedHostList: [{
        id: 1,
        version: '4.3.0.0'
      }],
      listOfServices: [
        {
          name: 'broker'
        },
        {
          name: 'endpoint'
        }
      ]
    },
    overview: {}
  },
  endpointQuery: {}
});
test('areSomeScanning', function(assert) {
  const result = areSomeScanning(STATE);
  assert.equal(result, true, 'should return true as some are scanning');
});

test('hostExportLink', function(assert) {
  const result = hostExportLink(STATE);
  assert.equal(result, `${location.origin}/rsa/endpoint/machine/property/download?id=123`, 'should return the export link');
});

test('hostExportLink when serverId is defined', function(assert) {
  const result = hostExportLink({ ...STATE, endpointQuery: { serverId: '1234' } });
  assert.equal(result, `${location.origin}/rsa/endpoint/1234/machine/property/download?id=123`, 'should return the export link which includes serverId');
});

test('isAllHostSelected', function(assert) {
  const result = isAllHostSelected(STATE);
  assert.equal(result, false);
});

test('noHostsSelected', function(assert) {
  const result = noHostsSelected(STATE);
  assert.equal(result, false);
});

test('processedHostList', function(assert) {
  const result = processedHostList(STATE);
  assert.equal(result.length, 2);
  assert.equal(result[0].canStartScan, false);
  assert.equal(result[0].isMFTEnabled, false);
  assert.equal(result[0].isAgentRoaming, true);

  assert.equal(result[1].canStartScan, true);
  assert.equal(result[1].isMFTEnabled, true);
  assert.equal(result[1].isAgentRoaming, false);
});

test('serviceList', function(assert) {
  const result = serviceList(STATE);

  assert.equal(result.length, 1);
});

test('allAreEcatAgents, check all are 4.4 agents', function(assert) {
  const result = allAreEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: '1',
            version: '4.4.0.0'
          },
          {
            id: '2',
            version: '4.4.0.1'
          }
        ]
      },
      overview: {}
    }
  }));
  assert.equal(result, true);
});

test('allAreEcatAgents, when some are not ecat agents', function(assert) {
  const result = allAreEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: '1',
            version: '4.2.0.0'
          },
          {
            id: '2',
            version: '4.4.0.1'
          }
        ]
      },
      overview: {}
    }
  }));
  assert.equal(result, false);
});

test('allAreEcatAgents, when no host is selected', function(assert) {
  const result = allAreEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: []
      },
      overview: {}
    }
  }));
  assert.equal(result, true);
});

test('allAreEcatAgents, when no are ecat agent', function(assert) {
  const result = allAreEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: '1',
            version: '11.4.0.0'
          },
          {
            id: '2',
            version: '4.2.0.1'
          }
        ]
      },
      overview: {}
    }
  }));
  assert.equal(result, false);
});

test('hasEcatAgents, check some are 4.4 agents', function(assert) {
  const result = hasEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: '1',
            version: '4.4.0.0'
          },
          {
            id: '2',
            version: '4.2.0.1'
          }
        ]
      },
      overview: {}
    }
  }));
  assert.equal(result, true);
});

test('hasEcatAgents, when none are ecat agents', function(assert) {
  const result = hasEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: '1',
            version: '11.1.0.0'
          },
          {
            id: '2',
            version: '4.2.0.1'
          }
        ]
      },
      overview: {}
    }
  }));
  assert.equal(result, false);
});

test('hasEcatAgents, when no host is selected', function(assert) {
  const result = hasEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: []
      },
      overview: {}
    }
  }));
  assert.equal(result, false);
});

test('warningMessages', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: [ { id: 1, version: '4.4', managed: false }]
      },
      overview: {}
    }
  });
  const result = warningMessages(state);
  assert.equal(result.length, 3, 'should contain three error message');

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'idle'
            }
          }
        ],
        selectedHostList: [ { id: 1, version: '4.4', managed: true }]
      },
      overview: {}
    }
  });
  assert.equal(warningMessages(state2).length, 1, 'should contain one error message');

  const state3 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: [ { id: 1, version: '11.1', managed: false }]
      },
      overview: {}
    }
  });
  assert.equal(warningMessages(state3).length, 2, 'should contain two error messages');

  const state4 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'idle'
            }
          }
        ],
        selectedHostList: [ { id: 1, version: '11.1', managed: true }]
      },
      overview: {}
    }
  });
  assert.equal(warningMessages(state4).length, 0, 'should contain zero error message');
});

test('isScanStartButtonDisabled', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: []
      },
      overview: {}
    }
  });
  const result = isScanStartButtonDisabled(state);
  assert.equal(result, true, 'should be true');

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: [{ id: 1, managed: true, scanStatus: 'idle' }]
      },
      overview: {}
    }
  });
  assert.equal(isScanStartButtonDisabled(state2), false, 'should be false when some host are selected');

  const state3 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: new Array(200)
      },
      overview: {}
    }
  });
  assert.equal(isScanStartButtonDisabled(state3), true, 'should be true when more host selected');

  const state4 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: [{ id: 1, managed: false }]
      },
      overview: {}
    }
  });
  assert.equal(isScanStartButtonDisabled(state4), true, 'should be true when selected host is migrated');

  const state5 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [],
        selectedHostList: []
      },
      overview: {}
    }
  });
  assert.equal(isScanStartButtonDisabled(state5), true, 'should be true when host list is empty');
});

test('isScanStopButtonDisabled', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: []
      },
      overview: {}
    }
  });
  const result = isScanStopButtonDisabled(state);
  assert.equal(result, true, 'should be true');

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ],
        selectedHostList: [{ id: 1, managed: true }]
      },
      overview: {}
    }
  });
  assert.equal(isScanStopButtonDisabled(state2), false, 'should be false when some host are selected');

});
test('extractAgentIds', function(assert) {
  const state1 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [ { id: 1, version: '4.4', managed: true }, { id: 2, version: '11.1', managed: false }]
      },
      overview: {}
    }
  });
  const result1 = extractAgentIds(state1);
  assert.equal(result1.length, 0, 'Should extract zero agent');

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [ { id: 1, version: '4.4', managed: true }, { id: 2, version: '11.1', managed: true }]
      },
      overview: {}
    }
  });
  const result2 = extractAgentIds(state2);
  assert.equal(result2.length, 1, 'Should extract zero agent');
});

test('isExportButtonDisabled', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {}
    },
    endpointServer: {},
    endpointQuery: {}
  });
  const result = isExportButtonDisabled(state);
  assert.equal(result.disabled, true, 'export button is disabled');
});

test('isExportButtonDisabled when server is endpoint broker server', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ]
      }
    },
    endpointServer: {
      serviceData: [{
        id: '123',
        name: 'endpoint-broker-server'
      }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result = isExportButtonDisabled(state);
  assert.equal(result.disabled, true, 'export button is disabled');
});

test('isExportButtonDisabled when endpoint server is empty object', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        hostList: [
          {
            id: 1,
            agentStatus: {
              scanStatus: 'scanning'
            }
          }
        ]
      }
    },
    endpointServer: {
      serviceData: [{
        id: '123',
        name: 'endpoint-server'
      }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result = isExportButtonDisabled(state);
  assert.equal(result.disabled, false, 'export button is enabled');
});

test('hostListPropertyTabs', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        activeHostListPropertyTab: 'RISK'
      }
    }
  });
  const result = hostListPropertyTabs(state).findBy('name', 'RISK');
  assert.equal(result.selected, true, 'RISK Tab should be selected');
});


test('hostTotalLabel', function(assert) {
  const state1 = Immutable.from({
    endpoint: {
      filter: {},
      machines: {
        totalItems: 1278
      }
    },
    endpointServer: { serviceData: [{ id: '123', name: 'endpoint-broker-server' }] },
    endpointQuery: { serverId: '123' }
  });
  const result1 = hostTotalLabel(state1);
  assert.equal(result1, '1000+');

  const state2 = Immutable.from({
    endpoint: {
      filter: { expressionList: Array(1) },
      machines: {
        totalItems: 1278
      }
    },
    endpointServer: {
      serviceData: [{ id: '123', name: 'endpoint-server' }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result2 = hostTotalLabel(state2);
  assert.equal(result2, '1278');

  const state3 = Immutable.from({
    endpoint: {
      filter: { expressionList: Array(1) },
      machines: {
        totalItems: 1278,
        hasNext: true
      }
    },
    endpointServer: {
      serviceData: [{ id: '123', name: 'endpoint-server' }]
    },
    endpointQuery: {
      serverId: '123'
    }
  });
  const result3 = hostTotalLabel(state3);
  assert.equal(result3, '1000+');

  const state4 = Immutable.from({
    endpoint: {
      filter: {},
      machines: {
        totalItems: 999
      }
    },
    endpointServer: {},
    endpointQuery: {}
  });
  const result4 = hostTotalLabel(state4);
  assert.equal(result4, '999');
});

test('nextLoadCount', function(assert) {
  const result1 = nextLoadCount(STATE);
  assert.equal(result1, 2);
  const hostList = new Array(101)
    .join().split(',')
    .map(function(item, index) {
      return { index: { id: ++index, checksumSha256: index } };
    });
  const STATE1 = Immutable.from({
    endpoint: {
      filter: {
      },
      machines: {
        totalItems: 101,
        hostList
      }
    }
  });
  const result2 = nextLoadCount(STATE1);
  assert.equal(result2, 100);
});

test('isInsightsAgent when focused agent is an insight agent', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        focusedHost: {
          id: 1,
          machineIdentity: {
            agentMode: 'insights'
          }
        }
      }
    }
  });
  const result = isInsightsAgent(state);
  assert.equal(result, true, 'focused agent is an insight agent');
});

test('isInsightsAgent when focused agent is an advanced agent', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        focusedHost: {
          id: 1,
          machineIdentity: {
            agentMode: 'advanced'
          }
        }
      }
    }
  });
  const result = isInsightsAgent(state);
  assert.equal(result, false, 'focused agent is not an insight agent');
});

test('isInsightsAgent when focused agent is undefined in current state', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        focusedHost: undefined
      }
    }
  });
  const result = isInsightsAgent(state);
  assert.equal(result, false, 'default value as false should return');
});

test('allAreMigratedHosts returns true if the selected agent has been migrated', function(assert) {
  const state1 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: 2,
            version: '4.3.0.0',
            managed: false
          }
        ]
      }
    }
  });
  const result1 = allAreMigratedHosts(state1);
  assert.equal(result1, true, 'Returns true as agent is migrated');

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: 1,
          version: '4.3.0.0',
          managed: true,
          scanStatus: 'idle'
        },
        {
          id: 2,
          version: '4.3.0.0',
          managed: true,
          scanStatus: 'idle'
        }]
      }
    }
  });
  const result2 = allAreMigratedHosts(state2);
  assert.equal(result2, false, 'Returns false as agent is not migrated');
});

test('actionsDisableMessage', function(assert) {
  const state1 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: []
      }
    }
  });
  const result1 = actionsDisableMessage(state1);
  assert.equal(result1, 'Select a host to perform this action.');

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: new Array(120)
      }
    }
  });
  const result2 = actionsDisableMessage(state2);
  assert.equal(result2, 'More than 100 hosts are selected.');

  const state3 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{ id: '1', version: '4.4', managed: true }]
      }
    }
  });
  const result3 = actionsDisableMessage(state3);
  assert.equal(result3, '4.4 agent(s) selected.');

  const state4 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{ id: '1', version: '4.5', managed: false }]
      }
    }
  });
  const result4 = actionsDisableMessage(state4);
  assert.equal(result4, 'Selected hosts not managed by the current server.');

  const state5 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{ id: '1', version: '4.5', managed: true, scanStatus: 'idle' }]
      }
    }
  });
  const result5 = actionsDisableMessage(state5);
  assert.equal(result5, 'Scan cannot be stopped as scan status is idle.');
  const state6 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{ id: '1', version: '4.5', managed: true, scanStatus: 'scanning' }]
      }
    }
  });
  const result6 = actionsDisableMessage(state6);
  assert.equal(result6, 'Selected hosts are already being scanned.');
  const state7 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{ id: '1', version: '4.5', managed: true, scanStatus: 'scanning' },
          { id: '2', version: '4.5', managed: true, scanStatus: 'idle' }]
      }
    }
  });
  const result7 = actionsDisableMessage(state7);
  assert.equal(result7, '');
});

test('mftDownloadButtonStatus', function(assert) {
  const state1 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'linux',
            agentMode: 'advanced'
          },
          isMFTEnabled: false,
          version: '11.4.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result1 = mftDownloadButtonStatus(state1);
  assert.deepEqual(result1, { isDisplayed: false });

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced'
          },
          isMFTEnabled: true,
          version: '11.3.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result2 = mftDownloadButtonStatus(state2);
  assert.deepEqual(result2, { isDisplayed: true });

  const state3 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced'
          },
          isMFTEnabled: false,
          version: '11.3.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result3 = mftDownloadButtonStatus(state3);
  assert.deepEqual(result3, { isDisplayed: false });
});

test('agentVersionSupported', function(assert) {
  const state0 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '12.1.0.0'
        }]
      }
    }
  });
  const result0 = agentVersionSupported(state0);
  assert.deepEqual(result0, true);

  const state1 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '11.4.0.0'
        }]
      }
    }
  });
  const result1 = agentVersionSupported(state1);
  assert.deepEqual(result1, true);

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '11.3.0.0'
        }]
      }
    }
  });
  const result2 = agentVersionSupported(state2);
  assert.deepEqual(result2, false);

  const state3 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '11.5.0.0'
        }]
      }
    }
  });
  const result3 = agentVersionSupported(state3);
  assert.deepEqual(result3, true);

  const state4 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '10.5.0.0'
        }]
      }
    }
  });
  const result4 = agentVersionSupported(state4);
  assert.deepEqual(result4, false);
});

test('agentVersionNotSupported', function(assert) {
  const state0 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '12.1.0.0'
        }]
      }
    }
  });
  const result0 = agentVersionNotSupported(state0);
  assert.deepEqual(result0, false, '12.1.0.0');

  const state1 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '11.4.0.0'
        }]
      }
    }
  });
  const result1 = agentVersionNotSupported(state1);
  assert.deepEqual(result1, false, '11.4.0.0');

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '11.3.0.0'
        }]
      }
    }
  });
  const result2 = agentVersionNotSupported(state2);
  assert.deepEqual(result2, false, '11.3.0.0');

  const state3 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '11.2.0.0'
        }]
      }
    }
  });
  const result3 = agentVersionNotSupported(state3);
  assert.deepEqual(result3, true, '11.2.0.0');

  const state4 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          version: '10.5.0.0'
        }]
      }
    }
  });
  const result4 = agentVersionNotSupported(state4);
  assert.deepEqual(result4, true, '10.5.0.0');
});

test('processedHostList', function(assert) {
  const state1 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'linux',
            agentMode: 'advanced',
            agentVersion: '11.4.0.0'
          },
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result1 = processedHostList(state1);
  assert.equal(result1[0].isMFTEnabled, false);

  const state2 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '11.3.0.0'
          },
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result2 = processedHostList(state2);
  assert.equal(result2[0].isMFTEnabled, false);

  const state3 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'insights',
            agentVersion: '11.4.0.0'
          },
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result3 = processedHostList(state3);
  assert.equal(result3[0].isMFTEnabled, false);

  const state4 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '15.0.0.0'
          },
          version: '15.0.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result4 = processedHostList(state4);
  assert.equal(result4[0].isMFTEnabled, true);

  const state5 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '11.4.0.0'
          },
          version: '11.4.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result5 = processedHostList(state5);
  assert.equal(result5[0].isMFTEnabled, true);

  const state6 = Immutable.from({
    endpoint: {
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          agentStatus: {
            lastSeen: 'RelayServer'
          },
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '11.4.0.0'
          },
          version: '11.4.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
      }
    }
  });
  const result6 = processedHostList(state6);
  assert.equal(result6[0].isMFTEnabled, false);
});

test('isAgentMigrated is not broker', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '11.4.0.0'
          },
          version: '11.4.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
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
      machines: {
        hostList: [{
          id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
          machineIdentity: {
            machineName: 'RAR113-EPS',
            machineOsType: 'windows',
            agentMode: 'advanced',
            agentVersion: '11.4.0.0'
          },
          version: '11.4.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }]
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

test('isolationComment test', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: 1, version: '4.4', managed: false,
          agentStatus: {
            isolationStatus: {
              isolate: true,
              comment: 'test',
              excludedIps: ['0.0.0.0']
            }
          }
        }]
      },
      overview: {}
    }
  });
  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: 1, version: '4.4', managed: false,
          agentStatus: {
            isolationStatus: {
              isolate: true,
              comment: 'test',
              excludedIps: ['0.0.0.0']
            }
          }
        }]
      },
      overview: {
        agentStatus: {
          isolationStatus: {
            isolate: true,
            comment: 'test',
            excludedIps: ['0.0.0.0']
          }
        }
      }
    }
  });
  const result = isolationComment(state);
  assert.equal(result, 'test');

  const result2 = isolationComment(state2);
  assert.equal(result2, 'test');
});

test('excludedIps test', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: 1, version: '4.4', managed: false,
          agentStatus: {
            isolationStatus: {
              isolate: true,
              comment: 'test',
              excludedIps: ['0.0.0.0']
            }
          }
        }]
      },
      overview: {}
    }
  });
  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: 1, version: '4.4', managed: false,
          agentStatus: {
            isolationStatus: {
              isolate: true,
              comment: 'test',
              excludedIps: ['0.0.0.0']
            }
          }
        }]
      },
      overview: {
        agentStatus: {
          isolationStatus: {
            isolate: true,
            comment: 'test',
            excludedIps: ['0.0.0.0']
          }
        }
      }
    }
  });
  const result = excludedIps(state);
  assert.equal(result[0], ['0.0.0.0']);

  const result2 = excludedIps(state2);
  assert.equal(result2[0], ['0.0.0.0']);
});

test('selectedHostDetails test', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [{
          id: 1, version: '4.4', managed: false,
          agentStatus: {
            isolationStatus: {
              isolate: true,
              comment: 'test',
              excludedIps: ['0.0.0.0']
            }
          }
        }]
      },
      overview: {}
    }
  });
  const state2 = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: []
      },
      overview: {
        agentStatus: {
          isolationStatus: {
            isolate: true,
            comment: 'test',
            excludedIps: ['0.0.0.0']
          }
        }
      }
    }
  });
  const result = selectedHostDetails(state);
  assert.equal(result.id, 1);

  const result2 = selectedHostDetails(state2);
  assert.deepEqual(result2, {});
});
