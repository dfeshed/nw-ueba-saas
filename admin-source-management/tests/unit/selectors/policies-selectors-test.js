import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';

import {
  isPolicyListLoading
} from 'admin-source-management/reducers/usm/policies-selectors';

module('Unit | Selectors | Policies Selectors');

const fullState = {
  usm: {
    policies: {
      items: [],
      itemsStatus: null
    }
  }
};

test('isPolicyListLoading selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.policies.itemsStatus = 'wait';
  assert.equal(isPolicyListLoading(Immutable.from(state)), true, 'isPolicyListLoading should return true when status is wait');

  state.usm.policies.itemsStatus = 'complete';
  assert.equal(isPolicyListLoading(Immutable.from(state)), false, 'isPolicyListLoading should return false when status is completed');
});
