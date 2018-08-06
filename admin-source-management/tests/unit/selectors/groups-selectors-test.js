import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import {
  groups,
  isGroupsLoading
} from 'admin-source-management/reducers/usm/groups-selectors';

const fullState = {
  usm: {
    groups: {
      items: [],
      itemsStatus: null // wait, complete, error
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

module('Unit | Selectors | Groups Selectors', function() {

  test('groups selector', function(assert) {
    const state = _.cloneDeep(fullState);
    state.usm.groups.items = fetchGroupsData;
    assert.deepEqual(groups(Immutable.from(state)), fetchGroupsData, 'The returned value from the groups selector is as expected');
  });

  test('isGroupsLoading selector', function(assert) {
    const state = _.cloneDeep(fullState);
    state.usm.groups.itemsStatus = 'wait';
    assert.equal(isGroupsLoading(Immutable.from(state)), true, 'isGroupsLoading should return true when status is wait');

    state.usm.groups.itemsStatus = 'complete';
    assert.equal(isGroupsLoading(Immutable.from(state)), false, 'isGroupsLoading should return false when status is completed');
  });

});
