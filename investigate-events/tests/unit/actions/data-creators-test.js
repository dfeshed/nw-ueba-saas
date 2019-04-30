import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import dataCreators from 'investigate-events/actions/data-creators';

module('Unit | Actions | Data-Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('setVisibleColumns action creator returns proper type', function(assert) {
    const { type } = dataCreators.setVisibleColumns();
    assert.equal(type, ACTION_TYPES.SET_VISIBLE_COLUMNS, 'action has the correct type');
  });

  test('updateGlobalPreferences action creator returns proper type', function(assert) {
    const { type } = dataCreators.updateGlobalPreferences();
    assert.equal(type, ACTION_TYPES.UPDATE_GLOBAL_PREFERENCES, 'action has the correct type');
  });

  test('updateSort action creator returns proper type', function(assert) {
    const { type } = dataCreators.updateSort();
    assert.equal(type, ACTION_TYPES.UPDATE_SORT, 'action has the correct type');
  });

  test('updateSummary call will update timerange if autoSummaryCall is enabled', function(assert) {
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().serviceId('10').hasSummaryData(true, '1').autoUpdateSummary(true).previouslySelectedTimeRanges().build();
    };

    const timerangeDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SET_QUERY_TIME_RANGE, 'action has the correct type');
    };

    const dispatchUpdateSummary = (action) => {
      if (typeof action === 'function') {
        const thunk2 = action;
        thunk2(timerangeDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.SUMMARY_UPDATE, 'action has the correct type');
        done();
      }
    };
    const thunk = dataCreators.updateSummaryData();
    thunk(dispatchUpdateSummary, getState);
  });

  test('updateSummary call will not update timerange if autoSummaryCall is disabled', function(assert) {
    assert.expect(1);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().serviceId('10').hasSummaryData(true, '1').autoUpdateSummary(false).previouslySelectedTimeRanges().build();
    };

    const timerangeDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SET_QUERY_TIME_RANGE, 'action has the correct type');
    };

    const dispatchUpdateSummary = (action) => {
      if (typeof action === 'function') {
        const thunk2 = action;
        thunk2(timerangeDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.SUMMARY_UPDATE, 'action has the correct type');
        done();
      }
    };
    const thunk = dataCreators.updateSummaryData();
    thunk(dispatchUpdateSummary, getState);
  });
});
