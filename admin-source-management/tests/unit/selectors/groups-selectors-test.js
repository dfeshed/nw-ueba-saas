import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import {
  isGroupsLoading,
  focusedGroup
} from 'admin-source-management/reducers/usm/groups-selectors';

const fullState = {
  usm: {
    groups: {
      items: [],
      itemsStatus: null // wait, complete, error
    }
  }
};

module('Unit | Selectors | Groups Selectors');

test('isGroupsLoading selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.groups.itemsStatus = 'wait';
  assert.equal(isGroupsLoading(Immutable.from(state)), true, 'isGroupsLoading should return true when status is wait');

  state.usm.groups.itemsStatus = 'complete';
  assert.equal(isGroupsLoading(Immutable.from(state)), false, 'isGroupsLoading should return false when status is completed');
});

test('focusedGroup selector', function(assert) {
  const focusedItemData = {
    id: 'id_001',
    name: 'focusedItemData 011',
    description: 'focusedItemData 011 of state.usm.groups'
  };
  const state = _.cloneDeep(fullState);
  state.usm.groups.focusedItem = { ...focusedItemData };
  assert.deepEqual(focusedGroup(Immutable.from(state)), focusedItemData, 'The returned value from the focusedGroup selector is as expected');
});
