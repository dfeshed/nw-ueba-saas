import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import { selectedTabComponent, getFileDetailTabs } from 'investigate-files/reducers/visuals/selectors';

module('Unit | selectors | visuals');

test('getFileDetailTabs', function(assert) {
  const state = Immutable.from({
    files: {
      visuals: {
        activeFileDetailTab: 'OVERVIEW'
      }
    }
  });
  const result = getFileDetailTabs(state).findBy('name', 'OVERVIEW');
  assert.equal(result.selected, true, 'OVERVIEW tab should be selected');
});

test('selectedTabComponent for default tab', function(assert) {
  const state = Immutable.from({
    files: { visuals: { } }
  });
  const result = selectedTabComponent(state);
  assert.equal(result, 'file-details/overview', 'returns the default tab component class');
});

test('selectedTabComponent for different tab', function(assert) {
  const state = Immutable.from({
    files: {
      visuals: {
        activeFileDetailTab: 'ANALYSIS'
      }
    }
  });
  const result = selectedTabComponent(state);
  assert.equal(result, undefined, 'returns the selected tab component class for ANALYSIS');
});