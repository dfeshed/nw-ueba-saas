import { module, test } from 'qunit';
import ACTION_TYPES from 'recon/actions/types';
import { patchSocket } from '../../helpers/patch-socket';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';
import * as DataCreators from 'recon/actions/data-creators';
import Immutable from 'seamless-immutable';

const { _cookieStore } = DataCreators;

const getStateWithNetworkEventType = () => {
  return Immutable.from({
    recon: {
      data: { eventType: null },
      meta: { meta: [['medium', 1]] },
      visuals: { currentReconView: { name: 'PACKET' } }
    }
  });
};

const getStateWithLogEventType = () => {
  return Immutable.from({
    recon: {
      data: { eventType: null },
      meta: { meta: [['medium', 32]] },
      visuals: { currentReconView: { name: 'PACKET' } }
    }
  });
};

const reconDispatch = (action) => {
  if (action.type) {
    setNewReconViewCalled = true;
  }
};

const getState = () => {
  return {
    recon: {
      visuals: { }
    }
  };
};

let setNewReconViewCalled = false;

module('Unit | Actions | Data Creators', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    DataCreators._authCookie.reconPrefInitialized = false;
    _cookieStore.persist({
      authenticated: {}
    });
  });

  hooks.afterEach(function() {
    _cookieStore.clear();
    DataCreators._authCookie.reconPrefInitialized = false;
  });

  test('test if preferences are initialized for the first time after login', function(assert) {
    const done = assert.async();
    assert.expect(3);
    const callback = DataCreators.determineReconView([]);
    assert.equal(typeof callback, 'function');
    patchSocket((method, modelName) => {
      assert.equal(method, 'getPreferences');
      assert.equal(modelName, 'investigate-events-preferences');
    });
    const dispatchFn = function(action) {
      if (action.type === ACTION_TYPES.RESET_PREFERENCES) {
        done();
      }
    };
    callback(dispatchFn, getState);
  });

  test('test that preferences are not set after the first time', function(assert) {
    const done = assert.async();
    _cookieStore.persist({ authenticated: { reconPrefInitialized: true } }).then(() => {
      const callback = DataCreators.determineReconView([]);
      assert.equal(typeof callback, 'function');
      const dispatchFn = function(action) {
        assert.notEqual(action.type, ACTION_TYPES.RESET_PREFERENCES, 'should not set preferences again');
        done();
      };
      callback(dispatchFn, getState);
    });
  });

  test('test that cookie store is read only once', function(assert) {
    const done = assert.async();
    const restoreSpy = sinon.spy(_cookieStore, 'restore');
    _cookieStore.persist({ authenticated: { reconPrefInitialized: true } }).then(() => {
      const callback = DataCreators.determineReconView([]);
      callback(() => ({}), getState);
      setTimeout(() => { // delay to allow cookieStore to be read.
        callback(() => ({}), getState);
        setTimeout(() => { // delay to allow cookieStore to be read.
          assert.equal(restoreSpy.callCount, 1, 'cookieStore.restore() should be called only once');
          restoreSpy.restore();
          done();
        }, 200);
      }, 200);
    });
  });

  test('test that on preferences update, new recon view is not set if not changed', function(assert) {
    const preferences = {
      eventAnalysisPreferences: {
        currentReconView: 'PACKET'
      }
    };

    const dispatchFn = (action) => {
      if (typeof action === 'function') {
        const setReconViewThunk = action;
        setReconViewThunk(reconDispatch, getStateWithNetworkEventType);
      } else {
        if (action.type === ACTION_TYPES.SET_PREFERENCES) {
          assert.deepEqual(action.payload, preferences);
        }
      }
    };

    const callback = DataCreators.reconPreferencesUpdated(preferences);
    callback(dispatchFn, getStateWithNetworkEventType);
    assert.equal(setNewReconViewCalled, false, 'action is not called if no change is made');
  });

  test('test that on preferences update, even if view changes, new recon view is not set if current event type is log', function(assert) {
    assert.expect(2);
    const preferences = {
      eventAnalysisPreferences: {
        currentReconView: 'FILE'
      }
    };

    const dispatchFn = (action) => {
      if (typeof action === 'function') {
        const setReconViewThunk = action;
        setReconViewThunk(reconDispatch, getStateWithLogEventType);
      } else {
        if (action.type === ACTION_TYPES.SET_PREFERENCES) {
          assert.deepEqual(action.payload, preferences);
        }
      }
    };
    const callback = DataCreators.reconPreferencesUpdated(preferences);
    callback(dispatchFn, getStateWithLogEventType);
    assert.equal(setNewReconViewCalled, false, 'setReconView action is not called when event type is log/endpoint');
  });

  test('test that on preferences update, if view changes, new recon view is set if current event type is not log/endpoint', function(assert) {
    assert.expect(2);
    const preferences = {
      eventAnalysisPreferences: {
        currentReconView: 'FILE'
      }
    };

    const dispatchFn = (action) => {
      if (typeof action === 'function') {
        const setReconViewThunk = action;
        setReconViewThunk(reconDispatch, getStateWithNetworkEventType);
      } else {
        if (action.type === ACTION_TYPES.SET_PREFERENCES) {
          assert.deepEqual(action.payload, preferences);
        }
      }
    };

    const callback = DataCreators.reconPreferencesUpdated(preferences);
    callback(dispatchFn, getStateWithNetworkEventType);
    assert.equal(setNewReconViewCalled, true, 'setReconView action is called when event type is not log/endpoint');
  });
});
