import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/filters/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
// import { LIFECYCLE } from 'redux-pack';
// import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | Filters');

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
    filter: {},
    areFilterLoading: null,
    activeFilter: null,
    expressionList: null,
    lastFilterAdded: null
  });
});

test('The RESET_HOST_FILTERS reset the filters', function(assert) {
  const previous = Immutable.from({
    activeFilter: null,
    areFilterLoading: 'start',
    isFilterReset: true,
    expressionList: EXPRESSION_LIST
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_FILTERS });
  assert.deepEqual(result.expressionList.length, 0, 'Expecting to clear the expressionList');
});
