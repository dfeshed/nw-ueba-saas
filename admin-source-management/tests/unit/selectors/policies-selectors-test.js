import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';

import {
  isPoliciesLoading,
  focusedPolicy
} from 'admin-source-management/reducers/usm/policies-selectors';

const fullState = {
  usm: {
    policies: {
      items: [],
      itemsStatus: null
    }
  }
};

module('Unit | Selectors | Policies Selectors');

test('isPoliciesLoading selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.policies.itemsStatus = 'wait';
  assert.equal(isPoliciesLoading(Immutable.from(state)), true, 'isPoliciesLoading should return true when status is wait');

  state.usm.policies.itemsStatus = 'complete';
  assert.equal(isPoliciesLoading(Immutable.from(state)), false, 'isPoliciesLoading should return false when status is completed');
});

test('focusedPolicy selector', function(assert) {
  const focusedItemData = {
    id: 'id_001',
    name: 'focusedItemData 011',
    description: 'focusedItemData 011 of state.usm.policies'
  };
  const state = _.cloneDeep(fullState);
  state.usm.policies.focusedItem = { ...focusedItemData };
  assert.deepEqual(focusedPolicy(Immutable.from(state)), focusedItemData, 'The returned value from the focusedPolicy selector is as expected');
});
