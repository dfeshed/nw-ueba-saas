import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import cloneDeep from 'lodash';
import {
  isPolicyListLoading,
  isPolicyLoading,
  currentPolicy,
  hasMissingRequiredData
} from 'admin-source-management/reducers/policy/selector';

module('Unit | Selectors | Policy Selectors');

const fullState = {
  policy: {
    policy: {
      id: null,
      name: null
    },
    policyList: [],
    policyStatus: null,
    policySaveStatus: null // wait, complete, error
  }
};

const policyData = {
  'id': 'policy_001',
  'name': 'Policy 001',
  'description': 'Policy Description'
};

test('isPolicyLoading selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.policy.policySaveStatus = 'wait';
  assert.equal(isPolicyLoading(Immutable.from(state)), true, 'isPolicyLoading should return true when status is wait');

  state.policy.policySaveStatus = 'complete';
  assert.equal(isPolicyLoading(Immutable.from(state)), false, 'isPolicyLoading should return false when status is complete');
});

test('hasMissingRequiredData selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.policy.policy = { ...policyData, name: null };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is null');

  state.policy.policy = { ...policyData, name: '' };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is an empty string');

  state.policy.policy = { ...policyData, name: '   ' };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is all whitespace');

  state.policy.policy = { ...policyData };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), false, 'hasMissingRequiredData should return false when populated');
});

test('currentPolicy selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.policy.policy = { ...policyData };
  assert.deepEqual(currentPolicy(Immutable.from(state)), policyData, 'The returned value from the policy selector is as expected');
});

test('isPolicyListLoading selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.policy.policyStatus = 'wait';
  assert.equal(isPolicyListLoading(Immutable.from(state)), true, 'isPolicyListLoading should return true when status is wait');

  state.policy.policyStatus = 'complete';
  assert.equal(isPolicyListLoading(Immutable.from(state)), false, 'isPolicyListLoading should return false when status is completed');
});