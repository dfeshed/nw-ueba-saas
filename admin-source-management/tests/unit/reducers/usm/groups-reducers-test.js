import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/groups-reducers';

const initialState = {
  items: [],
  itemsStatus: null // wait, complete, error
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

module('Unit | Reducers | Groups Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, initialState);
  });

  test('on FETCH_GROUPS start, itemsStatus is properly set', function(assert) {
    const expectedEndState = {
      ...initialState,
      itemsStatus: 'wait'
    };
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_GROUPS });
    const endState = reducers(Immutable.from(initialState), action);
    assert.deepEqual(endState, expectedEndState, 'itemsStatus is wait');
  });

  test('on FETCH_GROUPS success, groups & itemsStatus are properly set', function(assert) {
    const expectedEndState = {
      ...initialState,
      items: fetchGroupsData,
      itemsStatus: 'complete'
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_GROUPS,
      payload: { data: fetchGroupsData }
    });
    const endState = reducers(Immutable.from(initialState), action);
    assert.deepEqual(endState, expectedEndState, 'groups populated & itemsStatus is complete');
  });

});
