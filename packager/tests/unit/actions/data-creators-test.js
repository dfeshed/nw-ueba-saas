import { test, moduleFor } from 'ember-qunit';
import * as dataCreators from 'packager/actions/data-creators';
import ACTION_TYPES from 'packager/actions/types';
import { patchSocket } from '../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleFor('service:request', {
  beforeEach() {
    initialize(this);
  }
});

test('Test action creator for making server call for fetching packager config information.', function(assert) {
  const done = assert.async();
  assert.expect(7);
  const callback = dataCreators.getConfig();
  assert.equal(typeof callback, 'function');
  patchSocket((method, modelName) => {
    assert.equal(method, 'get');
    assert.equal(modelName, 'packager');
  });
  const dispatchFn = function(action) {
    action.promise.then((response) => {
      assert.ok(response.data.logCollectionConfig, 'LogCollectionConfig should not be null');
      assert.ok(response.data.packageConfig, 'PackageConfig should not be null');
      assert.ok(response.data.logCollectionConfig.channels.length > 0, 'There should be at least 1 channel filter');
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

test('Test action creator for getting devices', function(assert) {
  const done = assert.async();
  assert.expect(4);
  patchSocket((method, modelName) => {
    assert.equal(method, 'getServices');
    assert.equal(modelName, 'packager');
  });
  const action = dataCreators.getDevices();
  action.promise.then((response) => {
    assert.ok(response.data.length > 0, 'There should be atleast one device (Decoder/Log Collector)');
    done();
  });
  assert.equal(action.type, ACTION_TYPES.GET_DEVICES);
});

test('Test action creator for Updating UI field values', function(assert) {
  const action = dataCreators.saveUIState({});
  assert.equal(action.type, ACTION_TYPES.UPDATE_FIELDS);
});

test('Test action creator for resetting the form', function(assert) {
  const action = dataCreators.resetForm({});
  assert.equal(action.type, ACTION_TYPES.RESET_FORM);
});
