import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/filter/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

module('Unit | Reducers | Filter');

const EXPRESSION_LIST = [
  {
    propertyName: 'FILE.machineOsType',
    propertyValues: [
      {
        value: 'windows'
      }
    ],
    restrictionType: 'IN'
  },
  {
    restrictionType: 'IN',
    propertyName: 'FILE.firstFileName',
    propertyValues: null
  },
  {
    propertyName: 'FILE.agentVersion',
    restrictionType: 'IN',
    propertyValues: [
      {
        value: '5.0.0.0'
      }
    ]
  }
];

const response = [
  {
    'id': '5923c7cbd8d4ae128db98c98',
    'name': 'JAZZ_NWE_5_AGENTS',
    'filterType': 'FILE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'FILE.agentVersion',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': '5.0.0.0'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5923c7e5d8d4ae128db98c99',
    'name': 'windows_linux_mac_agents',
    'filterType': 'FILE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'FILE.machineOsType',
          'restrictionType': 'IN',
          'propertyValues': [
            {
              'value': 'windows'
            },
            {
              'value': 'linux'
            },
            {
              'value': 'mac'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  },
  {
    'id': '5923c812d8d4ae128db98c9a',
    'name': 'Server_Machine_Names',
    'filterType': 'FILE',
    'criteria': {
      'criteriaList': [],
      'expressionList': [
        {
          'propertyName': 'FILE.machineName',
          'restrictionType': 'LIKE',
          'propertyValues': [
            {
              'value': 'Server'
            }
          ]
        }
      ],
      'predicateType': 'AND'
    },
    'systemFilter': false
  }
];

const filter = [
  {
    name: 'Size4MB',
    id: 12345678,
    description: 'file size > 4 MB'
  }
];

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    filter: {},
    areFilesLoading: null,
    activeFilter: null,
    expressionList: null,
    lastFilterAdded: null,
    fileFilters: [],
    selectedFilterId: null,
    isSystemFilter: false
  });
});


test('The RESET_FILE_FILTERS reset the filters', function(assert) {
  const previous = Immutable.from({
    activeFilter: null,
    areFilesLoading: 'wait',
    isFilterReset: true,
    expressionList: EXPRESSION_LIST
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_FILE_FILTERS });
  assert.deepEqual(result.expressionList.length, 0, 'Expecting to clear the expressionList');
});

test('The GET_FILTER get the filters', function(assert) {
  const previous = Immutable.from({
    areFilesLoading: null,
    fileFilters: [],
    selectedFilterId: '5923c7e5d8d4ae128db98c99'
  });

  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_FILTER });
  const endState = reducer(previous, action);
  assert.deepEqual(endState, { areFilesLoading: 'sorting', fileFilters: [], selectedFilterId: '5923c7e5d8d4ae128db98c99' });
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_FILTER,
    payload: { data: response }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.expressionList[0].propertyName, 'FILE.machineOsType', 'expected expressionList to be updated');
});

test('The REMOVE_FILE_FILTER removes the filters', function(assert) {
  const previous = Immutable.from({
    areFilesLoading: 'sorting',
    lastFilterAdded: null,
    expressionList: EXPRESSION_LIST
  });
  const result = reducer(previous, { type: ACTION_TYPES.REMOVE_FILE_FILTER, payload: 'FILE.agentVersion' });
  assert.deepEqual(result.expressionList.length, 2, 'Expecting to remove the desired filter from the expressionList');
});

// panelId s used as payload, fnd out what panelId is...
test('The SET_ACTIVE_FILTER sets active filter', function(assert) {
  const previous = Immutable.from({
    activeFilter: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_ACTIVE_FILTER, payload: 'agentVersion' });
  assert.deepEqual(result.activeFilter, 'agentVersion', 'Expected to set active filter');
});

// used to add a system filter by setting the expressionList
test('The SET_EXPRESSION_LIST sets the expressionList', function(assert) {
  const previous = Immutable.from({
    expressionList: null
  });
  const result = reducer(previous, {
    type: ACTION_TYPES.SET_EXPRESSION_LIST,
    payload: EXPRESSION_LIST
  });
  assert.deepEqual(result.expressionList.length, 3, 'Expected the expressionList to be set');
});

// updating the custom filters list by taking the response from createCustomSearch api
test('The UPDATE_FILTER_LIST update the filter list', function(assert) {
  const previous = Immutable.from({
    fileFilters: []
  });

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.UPDATE_FILTER_LIST,
    payload: { data: filter }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.fileFilters.length, 1, 'expected one custom filter');

});

test('The SET_APPLIED_FILES_FILTER sets system filter flag', function(assert) {
  const previous = Immutable.from({
    selectedFilterId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_APPLIED_FILES_FILTER, payload: '5923c7e5d8d4ae128db98c99' });
  assert.deepEqual(result.selectedFilterId, '5923c7e5d8d4ae128db98c99', 'Expected to denote if a system filter or not');
});

test('The SET_SYSTEM_FILTER_FLAG sets system filter flag', function(assert) {
  const previous = Immutable.from({
    isSystemFilter: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_SYSTEM_FILTER_FLAG, payload: true });
  assert.deepEqual(result.isSystemFilter, true, 'Expected to denote if a system filter or not');
});

test('The ADD_FILE_FILTER sets expressionList and lastFilterAdded', function(assert) {
  const previous = Immutable.from({ expressionList: [] });
  const filterAdded = [
    {
      propertyName: 'entropy',
      propertyValues: [
        {
          value: 10
        }
      ],
      restrictionType: 'GREATER_THAN'
    }];

  const filterAddedResult = reducer(previous, { type: ACTION_TYPES.ADD_FILE_FILTER, payload: filterAdded[0] });
  assert.deepEqual(filterAddedResult.expressionList, filterAdded, 'expressionList updated with filter added');
  assert.equal(filterAddedResult.lastFilterAdded, 'entropy', 'Last filter added populated');

  const additionalFilterAdded = [
    {
      propertyName: 'firstFileName',
      propertyValues: null
    }
  ];

  const additionalFilterAddedResult = reducer(filterAddedResult, { type: ACTION_TYPES.ADD_FILE_FILTER, payload: additionalFilterAdded[0] });
  assert.deepEqual(additionalFilterAddedResult.expressionList, [...additionalFilterAdded, ...filterAdded], 'expressionList updated with the  second filter added');
  assert.equal(additionalFilterAddedResult.lastFilterAdded, 'firstFileName', 'Last filter added populated with second filter name');
});

test('The UPDATE_FILE_FILTER updates expressionList', function(assert) {
  const previous = Immutable.from({ expressionList: [] });
  const payloadData = [
    {
      propertyName: 'entropy',
      propertyValues: [
        {
          value: 10
        }
      ],
      restrictionType: 'GREATER_THAN'
    }];

  const result = reducer(previous, { type: ACTION_TYPES.UPDATE_FILE_FILTER, payload: payloadData[0] });
  assert.deepEqual(result.expressionList, payloadData, 'expressionList updated with first filter added');
  assert.equal(result.lastFilterAdded, null, 'lastFilterAdded reset to null');

  const payloadDataUpdated = [
    {
      propertyName: 'firstFileName',
      propertyValues: [
        {
          value: 'm'
        }
      ],
      restrictionType: 'LIKE'
    }
  ];

  const resultUpdated = reducer(result, { type: ACTION_TYPES.UPDATE_FILE_FILTER, payload: payloadDataUpdated[0] });
  assert.deepEqual(resultUpdated.expressionList, [...payloadDataUpdated, ...payloadData], 'expressionList updated with second filter added');
  assert.equal(resultUpdated.lastFilterAdded, null, 'lastFilterAdded reset to null');
});

