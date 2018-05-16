import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import cloneDeep from 'lodash';
import {
  group,
  isGroupLoading,
  hasMissingRequiredData
} from 'admin-source-management/selectors/group-selectors';

module('Unit | Selectors | Group Selectors');

const fullState = {
  usm: {
    group: {
      group: {
        id: null,
        name: null,
        description: null,
        createdBy: null,
        createdOn: null,
        lastModifiedBy: null,
        lastModifiedOn: null
      },
      groupSaveStatus: null // wait, complete, error
    }
  }
};

const saveGroupData = {
  'id': 'group_001',
  'name': 'Zebra 001',
  'description': 'Zebra 001 of group group_001',
  'createdBy': 'local',
  'createdOn': 1523655354337,
  'lastModifiedBy': 'local',
  'lastModifiedOn': 1523655354337
};

test('group selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.usm.group.group = { ...saveGroupData };
  assert.deepEqual(group(Immutable.from(state)), saveGroupData, 'The returned value from the group selector is as expected');
});

test('isGroupLoading selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.usm.group.groupSaveStatus = 'wait';
  assert.equal(isGroupLoading(Immutable.from(state)), true, 'isGroupLoading should return true when status is wait');

  state.usm.group.groupSaveStatus = 'complete';
  assert.equal(isGroupLoading(Immutable.from(state)), false, 'isGroupLoading should return false when status is complete');
});

test('hasMissingRequiredData selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.usm.group.group = { ...saveGroupData, name: null };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is null');

  state.usm.group.group = { ...saveGroupData, name: '' };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is an empty string');

  state.usm.group.group = { ...saveGroupData, name: '   ' };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is all whitespace');

  state.usm.group.group = { ...saveGroupData };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), false, 'hasMissingRequiredData should return false when populated');
});
