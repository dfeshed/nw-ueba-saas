import { module, test } from 'qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import policyAPI from 'admin-source-management/actions/api/policy-api';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Unit | Utility | policy-api', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it creates the propery query for the fetchEndpointServers API method', async function(assert) {
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'fetchEndpointServers');
      assert.equal(modelName, 'policy');
      assert.deepEqual(query, {});
    });
    policyAPI.fetchEndpointServers();
  });

  test('it creates the propery query for the fetchLogServers API method', async function(assert) {
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'fetchLogServers');
      assert.equal(modelName, 'policy');
      assert.deepEqual(query, {});
    });
    policyAPI.fetchLogServers();
  });

  test('it creates the propery query for the fetchFileSourceTypes API method', async function(assert) {
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'fetchFileSourceTypes');
      assert.equal(modelName, 'policy');
      assert.deepEqual(query, {});
    });
    policyAPI.fetchFileSourceTypes();
  });
});