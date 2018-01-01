import { module, test } from 'qunit';
import * as dataCreators from 'recon/actions/data-creators';
import CookieStore from 'component-lib/session-stores/application';
import ACTION_TYPES from 'recon/actions/types';
import { patchSocket } from '../../helpers/patch-socket';

const cookieStore = CookieStore.create();

module('Unit | Actions | Data Creators', {
  before() {
    cookieStore.persist({
      authenticated: {}
    });
  },
  after() {
    cookieStore.clear();
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
  cookieStore.persist({ authenticated: { reconPrefInitialized: true } }).then(() => {
    const callback = dataCreators.determineReconView([]);
    assert.equal(typeof callback, 'function');
    const dispatchFn = function(action) {
      assert.notEqual(action.type, ACTION_TYPES.RESET_PREFERENCES, 'should not set preferences again');
      done();
    };
    callback(dispatchFn, getState);
  });
});
