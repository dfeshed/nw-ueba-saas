import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import cloneDeep from 'lodash';
import {
  groups,
  isGroupsLoading
} from 'admin-source-management/selectors/groups-selectors';

module('Unit | Selectors | Groups Selectors');

const fullState = {
  usm: {
    groups: {
      groups: [],
      groupsStatus: null // wait, complete, error
    }
  }
};

const fetchGroupsData = [
  {
    'id': 'group_001',
    'name': 'Zebra 001',
    'description': 'Zebra 001 of group group_001',
    'createdBy': 'local',
    'createdOn': 1523655354337,
    'lastModifiedBy': 'local',
    'lastModifiedOn': 1523655354337
  }
];

test('groups selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.usm.groups.groups = fetchGroupsData;
  assert.deepEqual(groups(Immutable.from(state)), fetchGroupsData, 'The returned value from the groups selector is as expected');
});

test('isGroupsLoading selector', function(assert) {
  const state = cloneDeep(fullState).value();
  state.usm.groups.groupsStatus = 'wait';
  assert.equal(isGroupsLoading(Immutable.from(state)), true, 'isGroupsLoading should return true when status is wait');

  state.usm.groups.groupsStatus = 'complete';
  assert.equal(isGroupsLoading(Immutable.from(state)), false, 'isGroupsLoading should return false when status is completed');
});
