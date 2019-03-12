import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import {
  preferencesUpdated
} from 'investigate-events/actions/preferences-creators';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import * as reconDataCreators from 'recon/actions/data-creators';
import sinon from 'sinon';

module('Unit | Actions | preferences creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });
  test('preferencesUpdated updates all preferences for INVESTIGATE_EVENTS and only one preference for RECON', function(assert) {
    const getState = () => {
      return new ReduxDataHelper().isQueryRunning(true).defaultEventAnalysisPreferences().build();
    };
    const preferences = { eventAnalysisPreferences: { defaultLogFormat: 'JSON' } };

    const actionOrThunk = preferencesUpdated(preferences);

    const reconPreferencesUpdatedMock = sinon.stub(reconDataCreators, 'reconPreferencesUpdated');

    const downstreamDispatch = (actionOrThunk) => {
      if (typeof actionOrThunk === 'function') {
        // is another thunk, recurse
        actionOrThunk(downstreamDispatch, getState);
      } else {

        if (actionOrThunk && actionOrThunk.type === ACTION_TYPES.SET_PREFERENCES) {
          const currentEventAnalysisPreferences = getState().investigate.data.eventAnalysisPreferences;
          assert.equal(Object.keys(currentEventAnalysisPreferences).length, Object.keys(actionOrThunk.payload).length, 'All the eventAnalysis preferences updated in Investigate state');
          assert.equal(actionOrThunk.payload.defaultLogFormat, preferences.eventAnalysisPreferences.defaultLogFormat, 'Correct eventAnalysis preference updated in Investigate state');
        }
      }
    };

    downstreamDispatch(actionOrThunk, getState);
    assert.deepEqual(reconPreferencesUpdatedMock.getCall(0).args[0], preferences, 'Only the one altered preference is updated in recon');
  });
});
