import { module, test } from 'qunit';
import { activeTabName } from 'investigate-users/reducers/tabs/selectors';

module('Unit | Selector | Tabs Selector');

const state = {
  tabs: {
    activeTabName: 'overview'
  }
};

test('test active tab name', function(assert) {
  assert.equal(activeTabName(state), 'overview');
});