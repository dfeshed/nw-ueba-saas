import { test, module, setupTest } from 'ember-qunit';
import * as dataCreators from 'endpoint-rar/actions/data-creators';
import ACTION_TYPES from 'endpoint-rar/actions/types';
import { patchSocket } from '../../helpers/patch-socket';

module('Unit | Actions | Data Creators', function(hooks) {

  setupTest(hooks);

  test('Test data creator for making server call for fetching RAR id.', function(assert) {
    const done = assert.async();
    assert.expect(4);
    const callback = dataCreators.getRARDownloadID({ password: 'test' }, function() {}, 'serverId32324');
    assert.equal(typeof callback, 'function');
    patchSocket((method, modelName) => {
      assert.equal(method, 'rarInstaller');
      assert.equal(modelName, 'endpoint-rar');
    });
    const dispatchFn = function(action) {
      assert.equal(action.type, ACTION_TYPES.GET_RAR_INSTALLER_ID);
      done();
    };
    callback(dispatchFn);
  });

});