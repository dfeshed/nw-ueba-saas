import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  selectedAutorunTab,
  getAutorunTabs,
  getHostDetailTabs,
  hasMachineId,
  getHostPropertyTab,
  getDataSourceTab,
  getRiskPanelActiveTab,
  getContext,
  selectedTabComponent } from 'investigate-hosts/reducers/visuals/selectors';

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
      }
    }
  });
  const result = getHostDetailTabs(state).findBy('name', 'FILES');
  assert.equal(result.selected, true, 'FILES Tab should be selected');
});

test('getHostPropertyTab', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeHostPropertyTab: 'HOST'
      }
    }
  });
  const result = getHostPropertyTab(state).findBy('name', 'HOST');
  assert.equal(result.selected, true, 'HOST Tab should be selected');
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

test('getRiskPanelActiveTab - Host Property Tab is active', function(assert) {
  const state = Immutable.from({
    endpoint: {
      detailsInput: { agentId: 'abc' },
      visuals: { activeHostPropertyTab: 'HOST', activeDataSourceTab: 'ALERT' }
    }
  });
  const result = getRiskPanelActiveTab(state);
  assert.equal(result, 'HOST', 'Host property tab is active');
});

test('getRiskPanelActiveTab - Datasource Property Tab is active', function(assert) {
  const state = Immutable.from({
    endpoint: {
      detailsInput: { agentId: '' },
      visuals: { activeHostPropertyTab: 'HOST', activeDataSourceTab: 'ALERT' }
    }
  });
  const result = getRiskPanelActiveTab(state);
  assert.equal(result, 'ALERT', 'Datasource property tab is active');
});

test('getDataSourceTab', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeDataSourceTab: 'INCIDENT'
      }
    }
  });
  const result = getDataSourceTab(state).findBy('name', 'INCIDENT');
  assert.equal(result.selected, true, 'Incidents Tab should be selected');
});

test('getSelectedDetailTab', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeHostDetailTab: 'FILES'
      }
    }
  });
  const result = getHostDetailTabs(state).findBy('name', 'FILES');
  assert.equal(result.selected, true, 'FILES Tab should be selected');
});

test('getContext returns incidents', function(assert) {
  // If agentId is present, activeHostPropertyTab will be used
  const state = Immutable.from({
    endpoint: {
      detailsInput: { agentId: 'abc' },
      visuals: {
        lookupData: [{
          Incidents: {
            resultList: [{ _id: 'INC-18409', name: 'RespondAlertsESA for user199' }]
          }
        }],
        activeHostPropertyTab: 'INCIDENT',
        activeDataSourceTab: 'ALERT'
      }
    }
  });
  const result = getContext(state);
  assert.equal(result.resultList.length, 1, '1 incidents are fetched');
});

test('getContext returns alerts', function(assert) {
  const state = Immutable.from({
    endpoint: {
      detailsInput: { agentId: '' },
      visuals: {
        lookupData: [
          {
            Alerts: {
              resultList: [{
                alert: { source: 'Event Stream Analysis 1' },
                incidentId: 'INC-18409'
              },
              {
                alert: { source: 'Event Stream Analysis 2' },
                incidentId: 'INC-18410'
              }]
            }
          }
        ],
        activeHostPropertyTab: 'INCIDENT',
        activeDataSourceTab: 'ALERT'
      }
    }
  });
  const result = getContext(state);
  assert.equal(result.resultList.length, 2, '2 Alerts fetched');
});

test('selectedTabComponent', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeHostDetailTab: 'DRIVERS'
      }
    }
  });
  const result = selectedTabComponent(state);
  assert.equal(result, 'host-detail/drivers', 'returns the selected tab component class');
});
