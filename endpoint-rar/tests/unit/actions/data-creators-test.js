import { test, module, setupTest } from 'ember-qunit';
import * as dataCreators from 'endpoint-rar/actions/data-creators';
import { patchSocket } from '../../helpers/patch-socket';

module('Unit | Actions | Data Creators', function(hooks) {

  setupTest(hooks);

  test('Test data creator for making server call for fetching RAR id.', function(assert) {

    assert.expect(2);

    patchSocket((method, modelName) => {
      assert.equal(method, 'rarInstaller');
      assert.equal(modelName, 'endpoint-rar');
    });

    dataCreators.getRARDownloadID({ password: 'test' }, function() {}, 'serverId32324');
  });

  test('Test data creator for making server call for fetching RAR config data.', function(assert) {

    assert.expect(2);

    patchSocket((method, modelName) => {
      assert.equal(method, 'get');
      assert.equal(modelName, 'endpoint-rar');
    });

    dataCreators.getRARConfig(function() {});
  });

  test('Test data creator for making server call for saving RAR config data.', function(assert) {
    const data = {
      esh: 'esh-domain',
      servers: [
        {
          address: 'localhost',
          httpsPort: 443,
          httpsBeaconIntervalInSeconds: 900
        }
      ],
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 900
    };

    assert.expect(2);

    patchSocket((method, modelName) => {
      assert.equal(method, 'set');
      assert.equal(modelName, 'endpoint-rar');
    });

    dataCreators.saveRARConfig(data, function() {});
  });

});