import { test, module, setupTest } from 'ember-qunit';
import * as dataCreators from 'packager/actions/data-creators';
import ACTION_TYPES from 'packager/actions/types';
import { patchSocket } from '../../helpers/patch-socket';

module('Unit | Actions | Data Creators', function(hooks) {

  setupTest(hooks);

  test('Test action creator for making server call for fetching packager config information.', function(assert) {
    const done = assert.async();
    assert.expect(5);
    const callback = dataCreators.getConfig();
    assert.equal(typeof callback, 'function');
    patchSocket((method, modelName) => {
      assert.equal(method, 'get');
      assert.equal(modelName, 'packager');
    });
    const dispatchFn = function(action) {
      action.promise.then((response) => {
        assert.ok(response.data, 'PackageConfig should not be null');
        done();
      });
      assert.equal(action.type, ACTION_TYPES.GET_INFO);
    };
    callback(dispatchFn);
  });

  test('Test action creator for generating agent/log configuration.', function(assert) {
    const done = assert.async();
    assert.expect(4);
    const callback = dataCreators.setConfig({}, 'LOG_CONFIG');
    assert.equal(typeof callback, 'function');
    patchSocket((method, modelName) => {
      assert.equal(method, 'set');
      assert.equal(modelName, 'packager');
    });
    const dispatchFn = function(action) {
      assert.equal(action.type, ACTION_TYPES.SAVE_INFO);
      done();
    };
    callback(dispatchFn);
  });

  test('Test action creator for getEndpointServerList', function(assert) {
    const done = assert.async();
    const callback = dataCreators.getEndpointServerList('LOG_CONFIG');
    assert.equal(typeof callback, 'function');
    patchSocket((method, modelName) => {
      assert.equal(method, 'getEndpointServers');
      assert.equal(modelName, 'endpoint-server');
    });
    const dispatchFn = function(action) {
      assert.equal(action.type, ACTION_TYPES.GET_ENDPOINT_SERVERS);
      done();
    };
    callback(dispatchFn);
  });

  test('Test action creator for Updating UI field values', function(assert) {
    const action = dataCreators.saveUIState({});
    assert.equal(action.type, ACTION_TYPES.UPDATE_FIELDS);
  });

  test('Test action creator for resetting the form', function(assert) {
    const action = dataCreators.resetForm({});
    assert.equal(action.type, ACTION_TYPES.RESET_FORM);
  });

});
