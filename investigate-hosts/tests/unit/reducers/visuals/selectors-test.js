import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  selectedAutorunTab,
  getAutorunTabs,
  getHostDetailTabs,
  hasMachineId,
  selectedTabComponent,
  getPropertyPanelTabs,
  riskState } from 'investigate-hosts/reducers/visuals/selectors';

module('Unit | selectors | visuals');

test('getAutorunTabs', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeAutorunTab: 'SERVICES'
      }
    }
  });
  const result = getAutorunTabs(state).findBy('name', 'SERVICES');
  assert.equal(result.selected, true, 'SERVICES tab should be selected');
});

test('selectedAutorunTab', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeAutorunTab: 'SERVICES'
      }
    }
  });
  const result = selectedAutorunTab(state);
  assert.equal(result.name, 'SERVICES', 'SERVICES tab should be selected');
});

test('getHostDetailTabs', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeHostDetailTab: 'FILES'
      },
      overview: {
        hostDetails: {
          machine: {
            machineOsType: 'windows'
          }
        }
      }
    }
  });
  const result = getHostDetailTabs(state).findBy('name', 'FILES');
  assert.equal(result.selected, true, 'FILES Tab should be selected');
});

test('hasMachineId returns true', function(assert) {
  const state = Immutable.from({
    endpoint: { detailsInput: { agentId: 'abc' } }
  });
  assert.equal(hasMachineId(state), true, 'Has machine Id');
});

test('hasMachineId returns false', function(assert) {
  const state = Immutable.from({
    endpoint: { detailsInput: { agentId: '' } }
  });
  assert.equal(hasMachineId(state), false, 'Does not Have machine Id');
});

test('selectedTabComponent', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeHostDetailTab: 'DRIVERS'
      },
      overview: {
        hostDetails: {
          machine: {
            machineOsType: 'windows'
          }
        }
      }
    }
  });
  const result = selectedTabComponent(state);
  assert.equal(result, 'host-detail/drivers', 'returns the selected tab component class');
});

test('riskState', function(assert) {
  const riskStateResult = {
    riskScoreContext: {
      id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
      distinctAlertCount: {},
      categorizedAlerts: {
        All: {}
      }
    }
  };
  const state = Immutable.from({
    endpoint: {
      risk: {
        riskScoreContext: {
          id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
          distinctAlertCount: {},
          categorizedAlerts: {
            All: {}
          }
        }
      }
    }
  });
  const result = riskState(state);
  assert.deepEqual(result, riskStateResult, 'Both are equal');
});

test('getPropertyPanelTabs', function(assert) {
  const state1 = Immutable.from({
    endpoint: {
      visuals: {
        activePropertyPanelTab: 'POLICIES'
      },
      overview: {}
    }
  });
  const result1 = getPropertyPanelTabs(state1);
  assert.equal(result1.length, 2, 'tabs count');
  assert.equal(result1.findBy('name', 'POLICIES').selected, true, 'POLICIES tab is selected');

  const state2 = Immutable.from({
    endpoint: {
      visuals: {
        activePropertyPanelTab: null
      },
      overview: {
        hostDetails: {}
      }
    }
  });
  const result2 = getPropertyPanelTabs(state2);
  assert.equal(result2.length, 2, 'tabs count');

  const state3 = Immutable.from({
    endpoint: {
      visuals: {
        activePropertyPanelTab: null
      },
      overview: {
        hostDetails: {
          groupPolicy: {
            managed: true
          }
        }
      }
    }
  });
  const result3 = getPropertyPanelTabs(state3);
  assert.equal(result3.length, 2, 'tabs count');
  assert.equal(result3.findBy('name', 'HOST_DETAILS').selected, true, 'HOST_DETAILS tab is selected');

  const state4 = Immutable.from({
    endpoint: {
      visuals: {
        activePropertyPanelTab: null
      },
      overview: {
        hostDetails: {
          groupPolicy: {
            managed: false
          }
        }
      }
    }
  });
  const result4 = getPropertyPanelTabs(state4);
  assert.equal(result4.length, 1, 'tabs count');
});