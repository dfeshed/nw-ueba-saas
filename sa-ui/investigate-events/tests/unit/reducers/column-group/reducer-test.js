import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/column-group/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import EventColumnGroups from 'investigate-events/constants/OOTBColumnGroups';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';
import _ from 'lodash';

module('Unit | Reducers | column-group | Investigate');

const mappedColumnGroups = mapColumnGroupsForEventTable(EventColumnGroups);

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

  assert.deepEqual(newEndState.columnGroups, mappedColumnGroups, 'EventColumnGroups from server are mapped to UI columnGroups correctly');
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
  assert.deepEqual(newEndState.columnGroups, mappedColumnGroups);
  assert.equal(newEndState.columnGroups[0].columns[0].width, 175, 'time set to right value');
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
  assert.deepEqual(newEndState.columnGroups, mappedColumnGroups);
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
