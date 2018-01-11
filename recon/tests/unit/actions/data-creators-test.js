import { module, test } from 'qunit';
import * as dataCreators from 'recon/actions/data-creators';
import ACTION_TYPES from 'recon/actions/types';
import { patchSocket } from '../../helpers/patch-socket';
import sinon from 'sinon';

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
