import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/column-group/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import EventColumnGroups from 'investigate-events/constants/OOTBColumnGroups';
import _ from 'lodash';

module('Unit | Reducers | column-group | Investigate');

test('Should get column list from server', function(assert) {
  const previous = Immutable.from({
    columnGroup: null
  });

  // Need to reset width to null to simulate server call.
  const summaryColumnGroup = _.find(EventColumnGroups, { id: 'SUMMARY' });
  _.merge(_.find(summaryColumnGroup.columns, { field: 'custom.meta-summary' }), { width: null });
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.COLUMNS_RETRIEVE,
    payload: { data: EventColumnGroups }
  });
  const newEndState = reducer(previous, successAction);
  assert.deepEqual(newEndState.columnGroups, EventColumnGroups);
});

test('Should update the column widths if the right columns are present', function(assert) {
  const previous = Immutable.from({
    columnGroup: null
  });

  // Need to reset width to null to simulate server call.
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.COLUMNS_RETRIEVE,
    payload: { data: EventColumnGroups }
  });
  const newEndState = reducer(previous, successAction);
  assert.deepEqual(newEndState.columnGroups, EventColumnGroups);
  assert.equal(newEndState.columnGroups[0].columns[0].width, 135, 'time set to right value');
  assert.equal(newEndState.columnGroups[0].columns[4].width, 2000, 'summary set to right value');
});


test('Should show default column list in case of failure', function(assert) {
  const previous = Immutable.from({
    columnGroup: null
  });

  const successAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.COLUMNS_RETRIEVE,
    payload: { data: EventColumnGroups }
  });
  const newEndState = reducer(previous, successAction);
  assert.deepEqual(newEndState.columnGroups, EventColumnGroups);
});

test('Should sort column groups alphabetically irrespective of case', function(assert) {
  const previous = Immutable.from({
    columnGroup: null
  });

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.COLUMNS_RETRIEVE,
    payload: {
      data:
                [
                  { id: 1, name: 'Beta' },
                  { id: 2, name: 'alpha' }
                ]
    }
  });
  const newEndState = reducer(previous, successAction);
  const expectedResult = [
    { id: 2, name: 'alpha' },
    { id: 1, name: 'Beta' }
  ];
  assert.deepEqual(newEndState.columnGroups, expectedResult);
});

test('Should set relevant properties correctly after successfully creating new column group', function(assert) {
  const previous = Immutable.from({
    columnGroup: null
  });
  const colGroupName = `TEST-${Date.now().toString().substring(6)}`;
  const colGroupFields = [{
    field: 'time',
    title: 'Collection Time',
    position: 0,
    width: 135
  },
  {
    'field': 'service',
    'title': 'Service Name',
    'position': 1,
    'width': 100
  }];

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.COLUMNS_CREATE,
    payload: {
      data: {
        name: colGroupName,
        columns: colGroupFields
      }
    }
  });

  const newEndState = reducer(previous, successAction);
  const found = newEndState.columnGroups[0].columns.find((col) => col.field === colGroupFields[0].field);
  assert.ok(found, 'Failed to add new column group to state');
  assert.equal(newEndState.isColumnGroupsLoading, false, 'isColumnGroupsLoading shall be set false');
  assert.notOk(newEndState.createColumnGroupErrorCode, 'createColumnGroupErrorCode shall not be set');
  assert.notOk(newEndState.createColumnGroupErrorMessage, 'createColumnGroupErrorMessage shall not be set');
});

test('Should set relevant properties correctly after failure to create new column group', function(assert) {
  const previous = Immutable.from({
    columnGroup: {
      columnGroups: []
    }
  });

  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.COLUMNS_CREATE,
    payload: {
      meta: {
        message: 'TEST'
      },
      code: 999
    }
  });

  const newEndState = reducer(previous, failureAction);
  assert.equal(newEndState.isColumnGroupsLoading, false, 'isColumnGroupsLoading shall be set false');
  assert.ok(newEndState.createColumnGroupErrorCode, 'createColumnGroupErrorCode shall be set');
  assert.ok(newEndState.createColumnGroupErrorMessage, 'createColumnGroupErrorMessage shall be set');
});
