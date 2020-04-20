import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

import { getParamsForHashes, getHashForParams } from 'investigate-events/actions/fetch/query-hashes';
import { patchSocket } from '../../../helpers/patch-socket';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | API | query-hashes', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it creates the proper query for the getParamsForHashes API method', function(assert) {
    const done = assert.async();
    assert.expect(3);

    const hashes = ['1', '67', '22'];

    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'query-hashes');
      assert.equal(method, 'find');
      assert.deepEqual(query, { predicateIds: hashes });
      done();
    });

    getParamsForHashes(hashes);
  });

  test('it creates the proper query for the getHashForParams API method', function(assert) {
    const done = assert.async();
    assert.expect(3);

    const { investigate } = new ReduxDataHelper().language().pillsDataPopulated().build();
    const { pillsData } = investigate.queryNode;

    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'query-hashes');
      assert.equal(method, 'persist');
      assert.deepEqual(query, {
        predicateRequests: [
          { query: "a = 'x'" },
          { query: "b = 'y'" }
        ]
      });
      done();
    });

    getHashForParams(pillsData);
  });

});