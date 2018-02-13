import { module, test } from 'qunit';
import * as dataCreators from 'recon/actions/data-creators';
import ACTION_TYPES from 'recon/actions/types';
import { patchSocket } from '../../helpers/patch-socket';
import sinon from 'sinon';
import Immutable from 'seamless-immutable';

const { _cookieStore } = dataCreators;

module('Unit | Actions | Data Creators', {
  beforeEach() {
    dataCreators._authCookie.reconPrefInitialized = false;
    _cookieStore.persist({
      authenticated: {}
    });
  },
  afterEach() {
    _cookieStore.clear();
    dataCreators._authCookie.reconPrefInitialized = false;
  }
});

const getState = () => {
  return {
    recon: {
      visuals: { }
    }
  };
};

test('test if preferences are initialized for the first time after login', function(assert) {
  const done = assert.async();
  assert.expect(3);
  const callback = dataCreators.determineReconView([]);
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
    const callback = dataCreators.determineReconView([]);
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
    const callback = dataCreators.determineReconView([]);
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

const getStateWithNetworkEventType = () => {
  return Immutable.from({
    recon: {
      meta: {
        meta: []
      },
      visuals: {
        currentReconView: {
          name: 'PACKET'
        }
      }
    }
  });
};

test('test that on preferences update, new recon view is set only if changed', function(assert) {
  assert.expect(2);
  const preferences = {
    eventAnalysisPreferences: {
      currentReconView: 'PACKET'
    }
  };
  const dispatchFn = (action) => {
    if (action.type === ACTION_TYPES.SET_PREFERENCES) {
      assert.deepEqual(action.payload, preferences);
    }
  };
  if (!dataCreators.setNewReconView.isSinonProxy) {
    sinon.stub(dataCreators, 'setNewReconView');
  }
  const callback = dataCreators.reconPreferencesUpdated(preferences);
  callback(dispatchFn, getStateWithNetworkEventType);

  assert.equal(dataCreators.setNewReconView.called, false, 'setNewReconView is not expected to be called');
  dataCreators.setNewReconView.reset();
});

const getStateWithLogEventType = () => {
  return Immutable.from({
    recon: {
      meta: {
        meta: [
          ['medium', 32]
        ]
      },
      visuals: {
        currentReconView: {
          name: 'PACKET'
        }
      }
    }
  });
};

test('test that on preferences update, new recon view is not set if current event type is log', function(assert) {
  assert.expect(2);
  const preferences = {
    eventAnalysisPreferences: {
      currentReconView: 'PACKET'
    }
  };
  const dispatchFn = (action) => {
    if (action.type === ACTION_TYPES.SET_PREFERENCES) {
      assert.deepEqual(action.payload, preferences);
    }
  };
  if (!dataCreators.setNewReconView.isSinonProxy) {
    sinon.stub(dataCreators, 'setNewReconView');
  }
  const callback = dataCreators.reconPreferencesUpdated(preferences);
  callback(dispatchFn, getStateWithLogEventType);

  assert.equal(dataCreators.setNewReconView.called, false, 'setNewReconView is not expected to be called');
  dataCreators.setNewReconView.reset();
});