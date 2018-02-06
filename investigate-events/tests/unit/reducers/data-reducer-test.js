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

test('test REHYDRATE', function(assert) {
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

test('test REHYDRATE when state is not saved in local storage yet', function(assert) {
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
