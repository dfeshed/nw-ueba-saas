import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  selectedAutorunTab,
  getAutorunTabs,
  getHostDetailTabs,
  getHostPropertyTab,
  getDataSourceTab,
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

test('getDataSourceTab', function(assert) {
  const state = Immutable.from({
    endpoint: {
      visuals: {
        activeDataSourceTab: 'INCIDENTS'
      }
    }
  });
  const result = getDataSourceTab(state).findBy('name', 'INCIDENTS');
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
