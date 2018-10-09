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
  hostCountForDisplay,
  warningMessages,
  isScanStartButtonDisabled,
  extractAgentIds,
  isExportButtonDisabled } from 'investigate-hosts/reducers/hosts/selectors';

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
            scanStatus: 'scaning'
          },
          machine: {
            agentVersion: '4.3.0.0'
          }
        },
        {
          id: 2,
          agentStatus: {
            scanStatus: 'idle'
          },
          machine: {
            agentVersion: '4.4.0.0'
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
  assert.equal(result[1].canStartScan, true);
  assert.equal(result[0].selected, true);
  assert.equal(result[1].selected, false);
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

test('hostCountForDisplay', function(assert) {
  const result = hostCountForDisplay(STATE);
  assert.equal(result, 2, 'expected 2 machines');
  const newDisplay = hostCountForDisplay(Immutable.from({
    endpoint: {
      filter: {
        expressionList: [
          {
            propertyName: 'machine.machineOsType',
            propertyValues: [
              {
                value: 'windows'
              }
            ],
            restrictionType: 'IN'
          },
          {
            restrictionType: 'IN',
            propertyName: 'machine.agentVersion',
            propertyValues: null
          }
        ]
      },
      machines: {
        totalItems: '1000',
        hostList: [...Array(2000)]
      }
    }
  }));
  assert.equal(newDisplay, '1000+', 'expected 1000+ files');
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
        selectedHostList: [ { id: 1, version: '4.4' }]
      },
      overview: {}
    }
  });
  const result = warningMessages(state);
  assert.equal(result.length, 2, 'should contain two error message');

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
        selectedHostList: [ { id: 1, version: '4.4' }]
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
        selectedHostList: [ { id: 1, version: '11.1' }]
      },
      overview: {}
    }
  });
  assert.equal(warningMessages(state3).length, 1, 'should contain one error message');

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
        selectedHostList: [ { id: 1, version: '11.1' }]
      },
      overview: {}
    }
  });
  assert.equal(warningMessages(state4).length, 0, 'should contain one error message');
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
        selectedHostList: [{ id: 1 }]
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
});

test('extractAgentIds', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [ { id: 1, version: '4.4' }, { id: 2, version: '11.1' }]
      },
      overview: {}
    }
  });
  const result = extractAgentIds(state);
  assert.equal(result.length, 1, 'Should extract only one agent');
});

test('isExportButtonDisabled', function(assert) {
  const state = Immutable.from({
    endpoint: {
      machines: {
      }
    }
  });
  const result = isExportButtonDisabled(state);
  assert.equal(result, true, 'export button is disabled');
});

test('isExportButtonDisabled', function(assert) {
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
    }
  });
  const result = isExportButtonDisabled(state);
  assert.equal(result, false, 'export button is enabled');
});