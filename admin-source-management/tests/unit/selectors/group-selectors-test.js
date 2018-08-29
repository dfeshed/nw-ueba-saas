import { module, test, skip } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import {
  group,
  osTypes,
  selectedOsTypes,
  osDescriptions,
  selectedOsDescriptions,
  policies,
  selectedPolicy,
  isGroupLoading,
  hasMissingRequiredData
} from 'admin-source-management/reducers/usm/group-selectors';
import { initialState as _initialState } from 'admin-source-management/reducers/usm/group-reducers';
import policiesData from '../../../tests/data/subscriptions/policy/findAll/data';

const initialState = {
  ..._initialState
};

const fullState = {
  usm: {
    group: initialState
  }
};

const saveGroupData = {
  id: 'group_001',
  name: 'Zebra 001',
  description: 'Zebra 001 of group group_001',
  createdBy: 'local',
  createdOn: 1523655354337,
  lastModifiedBy: 'local',
  lastModifiedOn: 1523655354337,
  osTypes: [],
  osDescriptions: [],
  ipRangeStart: '192.168.10.1',
  ipRangeEnd: '192.168.10.10',
  assignedPolicies: null
};

module('Unit | Selectors | Group Selectors', function() {

  test('group selector', function(assert) {
    const state = _.cloneDeep(fullState);
    state.usm.group.group = { ...saveGroupData };
    assert.deepEqual(group(Immutable.from(state)), saveGroupData, 'The returned value from the group selector is as expected');
  });

  test('osTypes selector', function(assert) {
    const state = _.cloneDeep(fullState);
    const osTypesData = [...state.usm.group.osTypes];
    assert.deepEqual(osTypes(Immutable.from(state)), osTypesData, 'The returned value from the osTypes selector is as expected');
  });

  test('selectedOsTypes selector', function(assert) {
    const state = _.cloneDeep(fullState);
    // osTypes holds osType ID's only so use the first 2 ID's
    state.usm.group.group = { ...saveGroupData, osTypes: ['Windows', 'Mac'] };
    // the selector looks up osType objects by ID, so use the first 2 objects
    const selectedOsTypesData = [{ ...state.usm.group.osTypes[0] }, { ...state.usm.group.osTypes[1] }];
    assert.deepEqual(selectedOsTypes(Immutable.from(state)), selectedOsTypesData, 'The returned value from the selectedOsTypes selector is as expected');
  });

  test('osDescriptions selector', function(assert) {
    const state = _.cloneDeep(fullState);
    // osTypes holds osType ID's only so use the first 2 ID's
    state.usm.group.group = { ...saveGroupData, osTypes: ['Windows', 'Mac'] };
    // the selector looks up available osDescription objects for the selected osTypes,
    // so use all of the osDescription objects for the first 2 osTypes
    const osDescriptionsData = [...state.usm.group.osTypes[0].osDescriptions, ...state.usm.group.osTypes[1].osDescriptions];
    assert.deepEqual(osDescriptions(Immutable.from(state)), osDescriptionsData, 'The returned value from the osDescriptions selector is as expected');
  });

  test('selectedOsDescriptions selector', function(assert) {
    const state = _.cloneDeep(fullState);
    state.usm.group.group = {
      ...saveGroupData,
      // osTypes holds osType ID's only so use the first 2 ID's
      osTypes: ['Windows', 'Mac'],
      // osDescriptions holds osDescription ID's only
      // so use the first 2 osDescription ID's available for the selected osTypes
      osDescriptions: ['Windows Vista', 'Windows 7', 'Mac OS X 10.9', 'Mac OS X 10.10']
    };
    // the selector looks up osDescription objects by ID,
    // so use the first 2 osDescription objects available for the selected osTypes
    const selectedOsDescriptionsData = [
      { ...state.usm.group.osTypes[0].osDescriptions[0] },
      { ...state.usm.group.osTypes[0].osDescriptions[1] },
      { ...state.usm.group.osTypes[1].osDescriptions[0] },
      { ...state.usm.group.osTypes[1].osDescriptions[1] }
    ];
    assert.deepEqual(selectedOsDescriptions(Immutable.from(state)), selectedOsDescriptionsData, 'The returned value from the selectedOsDescriptions selector is as expected');
  });

  test('policies selector', function(assert) {
    const state = _.cloneDeep(fullState);
    state.usm.group.policies = [...policiesData];
    assert.deepEqual(policies(Immutable.from(state)), policiesData, 'The returned value from the policies selector is as expected');
  });

  skip('selectedPolicy selector', function(assert) {
    const state = _.cloneDeep(fullState);
    // policy holds map of { 'type': 'policyID' }
    state.usm.group.group = { ...saveGroupData, policy: { edrPolicy: 'policy_001' } };
    state.usm.group.policies = [...policiesData];
    // the selector looks up the policy object by the type:ID map, so use the first policy object
    const selectedPolicyData = { ...state.usm.group.policies[0] };
    assert.deepEqual(selectedPolicy(Immutable.from(state)), selectedPolicyData, 'The returned value from the selectedPolicy selector is as expected');
  });

  test('isGroupLoading selector', function(assert) {
    const state = _.cloneDeep(fullState);
    state.usm.group.groupStatus = 'wait';
    assert.equal(isGroupLoading(Immutable.from(state)), true, 'isGroupLoading should return true when groupStatus is wait');

    state.usm.group.groupStatus = 'complete';
    assert.equal(isGroupLoading(Immutable.from(state)), false, 'isGroupLoading should return false when groupStatus is complete');

    // reset groupStatus
    state.usm.group.groupStatus = null;

    state.usm.group.initGroupFetchPoliciesStatus = 'wait';
    assert.equal(isGroupLoading(Immutable.from(state)), true, 'isGroupLoading should return true when initGroupFetchPoliciesStatus is wait');

    state.usm.group.initGroupFetchPoliciesStatus = 'complete';
    assert.equal(isGroupLoading(Immutable.from(state)), false, 'isGroupLoading should return false when initGroupFetchPoliciesStatus is complete');
  });

  test('hasMissingRequiredData selector', function(assert) {
    const state = _.cloneDeep(fullState);
    state.usm.group.group = { ...saveGroupData, name: null };
    assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is null');

    state.usm.group.group = { ...saveGroupData, name: '' };
    assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is an empty string');

    state.usm.group.group = { ...saveGroupData, name: '   ' };
    assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is all whitespace');

    state.usm.group.group = { ...saveGroupData };
    assert.equal(hasMissingRequiredData(Immutable.from(state)), false, 'hasMissingRequiredData should return false when populated');
  });

});

