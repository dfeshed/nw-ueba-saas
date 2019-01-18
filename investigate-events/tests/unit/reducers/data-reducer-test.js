import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/data-reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
import EventColumnGroups from 'investigate-events/constants/OOTBColumnGroups';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import _ from 'lodash';

module('Unit | Reducers | data-reducer');

test('Should get column list from server', function(assert) {
  const previous = Immutable.from({
    columnGroups: null
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
    columnGroups: null
  });

  // Need to reset width to null to simulate server call.
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.COLUMNS_RETRIEVE,
    payload: { data: EventColumnGroups }
  });
  const newEndState = reducer(previous, successAction);
  assert.deepEqual(newEndState.columnGroups, EventColumnGroups);

  assert.equal(newEndState.columnGroups[0].columns[0].width, 135, 'time set to right value');
  assert.equal(newEndState.columnGroups[0].columns[4].width, 1000, 'summary set to right value');
});

test('Should show default column list in case of failure', function(assert) {
  const previous = Immutable.from({
    columnGroups: null
  });

  const successAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.COLUMNS_RETRIEVE,
    payload: { data: EventColumnGroups }
  });
  const newEndState = reducer(previous, successAction);
  assert.deepEqual(newEndState.columnGroups, EventColumnGroups);
});

test('REHYDRATE', function(assert) {
  const previous = Immutable.from({
    reconSize: RECON_PANEL_SIZES.MAX
  });

  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      investigate: {
        data: {
          reconSize: RECON_PANEL_SIZES.MIN
        }
      }
    }
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.reconSize, RECON_PANEL_SIZES.MIN);
});

test('REHYDRATE when state is not saved in local storage yet', function(assert) {
  const previous = Immutable.from({
    reconSize: RECON_PANEL_SIZES.MAX
  });

  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: { }
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.reconSize, RECON_PANEL_SIZES.MAX);
});

test('SET_PREFERENCES when columnGroup is present in the payload', function(assert) {
  const previous = Immutable.from({
    columnGroup: null
  });

  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      eventPreferences: {
        columnGroup: 'EMAIL'
      }
    }
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.columnGroup, 'EMAIL');
});

test('SET_PREFERENCES when columnGroup is not present in the payload and no column group is set currently', function(assert) {
  const previous = Immutable.from({
    columnGroup: null
  });

  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      eventPreferences: { }
    }
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.columnGroup, 'SUMMARY');
});

test('SET_PREFERENCES when columnGroup is not present in the payload and current group should be preserved', function(assert) {
  const previous = Immutable.from({
    columnGroup: 'SOME_GROUP'
  });

  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      eventPreferences: {
        // columnGroup: 'EMAIL'
      }
    }
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.columnGroup, 'SOME_GROUP');
});