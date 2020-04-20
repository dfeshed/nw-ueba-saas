import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/sources-reducers';

module('Unit | Reducers | Sources Reducers');

const initialState = {
  items: [],
  itemsStatus: null,
  itemsSelected: [],
  isFilterPanelOpen: true,
  isSelectAll: false,
  itemsTotal: null,
  itemsFilters: null,
  focusedItem: null,
  isTransactionUnderway: false,
  sortField: 'name',
  isSortDescending: false,
  groupRankingPrevListStatus: null
};

const fetchSourcesPayload = {
  data: {
    items: [
      {
        'id': 'source_001',
        'name': 'Zebra 001',
        'description': 'Zebra 001 of source source_001',
        'dirty': false
      }
    ],
    totalItems: 1
  }
};


test('should return the initial state', function(assert) {
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.SOURCES_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  const endState = reducers(Immutable.from(initialState), sortAction);
  assert.deepEqual(endState, initialState);
});

test('on FETCH_SOURCES start, source is reset and itemsStatus is properly set', function(assert) {
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.SOURCES_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  reducers(Immutable.from(initialState), sortAction);

  const expectedEndState = {
    ...initialState,
    itemsStatus: 'wait',
    items: []
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_SOURCES });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'source is set and itemsStatus is wait');
});

test('on FETCH_SOURCES success, source & itemsStatus are properly set', function(assert) {
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.SOURCES_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  reducers(Immutable.from(initialState), sortAction);

  const expectedEndState = {
    ...initialState,
    items: fetchSourcesPayload.data.items,
    itemsTotal: fetchSourcesPayload.data.totalItems,
    itemsStatus: 'complete',
    itemsRequest: undefined
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_SOURCES,
    payload: fetchSourcesPayload
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'source list populated & itemsStatus is complete');
});
