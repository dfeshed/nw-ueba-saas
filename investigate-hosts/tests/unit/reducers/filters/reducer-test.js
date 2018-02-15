import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/filters/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | Filters');

const deleteSearchResponsePayload = {
  'data': {
    'success': 'true',
    'id': 2
  },
  'request': {
    'data': {
      id: '2'
    }
  }
};

const EXPRESSION_LIST = [
  {
    propertyName: 'machine.machineOsType',
    propertyValues: [
      {
        value: 'windows'
      }
    ],
    restrictionType: 'IN'
  },
  {
    restrictionType: 'IN',
    propertyName: 'machine.agentVersion',
    propertyValues: null
  }
];

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    filters: null,
    activeFilter: null,
    expressionList: [],
    lastFilterAdded: null
  });
});

test('The RESET_HOST_FILTERS reset the filters', function(assert) {
  const previous = Immutable.from({
    activeFilter: null,
    isFilterReset: true,
    expressionList: EXPRESSION_LIST
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_FILTERS });
  assert.deepEqual(result.expressionList.length, 0, 'Expecting to clear the expressionList');
});

test('The DELETE_SAVED_SEARCH action deletes the search', function(assert) {
  const previous = Immutable.from({
    filters: [
      {
        id: 1,
        name: 'Unsigned'
      },
      {
        id: 2,
        name: 'Signed'
      }
    ]
  });
  const expectedEndState = {
    filters: [
      {
        id: 1,
        name: 'Unsigned'
      }
    ]
  };

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.DELETE_SAVED_SEARCH,
    payload: deleteSearchResponsePayload
  });
  const endState = reducer(previous, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The ADD_SYSTEM_FILTER adds the system filter to expression list', function(assert) {
  const previous = Immutable.from({
    expressionList: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.ADD_SYSTEM_FILTER, payload: [ { }] });
  assert.equal(result.expressionList.length, 1, 'Expecting to have one filter');
});

test('The SET_ACTIVE_FILTER sets currently active filter', function(assert) {
  const previous = Immutable.from({
    activeFilter: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_ACTIVE_FILTER, payload: 'hostName' });
  assert.equal(result.activeFilter, 'hostName', 'Expecting to have one filter');
});

test('The FETCH_ALL_SCHEMAS sets the searchable schema', function(assert) {
  const schemas = [
    {
      searchable: false
    },
    {
      searchable: true
    },
    {
      searchable: true
    },
    {
      searchable: false
    }
  ];
  const previous = Immutable.from({
    schemas: null
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_ALL_SCHEMAS,
    payload: { data: { fields: schemas } }
  });
  const result = reducer(previous, action);
  assert.equal(result.schemas.length, 2, 'Expecting to have two searchable columns');
});


test('The FETCH_ALL_FILTERS sets the filters to state', function(assert) {
  const filterList = [
    {
      filterType: 'MACHINE',
      id: 1,
      filterName: 'Test'
    },
    {
      filterType: 'FILE'
    },
    {
      filterType: 'FILE'
    },
    {
      filterType: 'MACHINE'
    }
  ];
  const previous = Immutable.from({
    filters: null,
    appliedHostFilter: 1
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_ALL_FILTERS,
    payload: { data: filterList }
  });
  const result = reducer(previous, action);
  assert.equal(result.filters.length, 2, 'Expecting to have two filters');
  assert.equal(result.filterSelected.filterName, 'Test');
});

test('The SET_APPLIED_HOST_FILTER set the selected filter', function(assert) {
  const previous = Immutable.from({
    customSearchVisible: false,
    appliedHostFilter: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_APPLIED_HOST_FILTER, payload: { isCustomFilter: true, filterId: 123 } });
  assert.equal(result.customSearchVisible, true);
  assert.equal(result.appliedHostFilter, 123);
});


test('The UPDATE_FILTER_LIST adds the new filter to list on add', function(assert) {
  const filterList = {
    filterType: 'MACHINE'
  };
  const previous = Immutable.from({
    filters: []
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.UPDATE_FILTER_LIST,
    payload: { data: filterList }
  });
  const result = reducer(previous, action);
  assert.equal(result.filters.length, 1, 'Expecting to have two filters');
});

test('The DELETE_SAVED_SEARCH deletes the filter from the state', function(assert) {
  const previous = Immutable.from({
    filters: [
      {
        id: 1
      },
      {
        id: 2
      },
      {
        id: 3
      }
    ]
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.DELETE_SAVED_SEARCH,
    payload: { data: { id: 2 } }
  });
  const result = reducer(previous, action);
  assert.equal(result.filters.length, 2, 'Expecting to have two filters');
});

test('The UPDATE_HOST_FILTER set the selected filter', function(assert) {
  const previous = Immutable.from({
    expressionList: [ {
      propertyName: 'test',
      expression: null
    }]
  });
  const result = reducer(previous, { type: ACTION_TYPES.UPDATE_HOST_FILTER, payload: { propertyName: 'test', expression: 'ip.src=1.1.1.1' } });
  assert.equal(result.expressionList.length, 1);
  assert.equal(result.expressionList[0].expression, 'ip.src=1.1.1.1');
});

test('The ADD_HOST_FILTER set the selected filter', function(assert) {
  const previous = Immutable.from({
    expressionList: [{
      propertyName: 'test',
      expression: null
    }]
  });
  const result = reducer(previous, { type: ACTION_TYPES.ADD_HOST_FILTER, payload: { propertyName: 'test', expression: 'ip.src=1.1.1.1' } });
  assert.equal(result.expressionList.length, 2);
});


test('The RESET_HOST_FILTERS set the selected filter', function(assert) {
  const previous = Immutable.from({
    activeFilter: 123,
    expressionList: new Array(1)
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_FILTERS });
  assert.equal(result.activeFilter, null);
  assert.equal(result.expressionList.length, 0);
});

test('The REMOVE_HOST_FILTER removes the selected filter', function(assert) {
  const previous = Immutable.from({
    activeFilter: 123,
    expressionList: [
      {
        propertyName: 'test'
      },
      {
        propertyName: 'test1'
      }
    ]
  });
  const result = reducer(previous, { type: ACTION_TYPES.REMOVE_HOST_FILTER, payload: 'test1' });
  assert.equal(result.expressionList.length, 1);
});
