import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import entityDetails from '../../data/presidio/user_details';
import { patchFetch } from '../../helpers/patch-fetch';
import { initializeEntityDetails } from 'entity-details/actions/entity-creators';
import dataIndex from '../../data/presidio';

module('Unit | Actions | Entity-creators Actions', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
  });

  test('it can intialize entity', (assert) => {
    assert.expect(3);
    const types = ['ENTITY_DETAILS::GET_ENTITY_DETAILS', 'ENTITY_DETAILS::INITIATE_ENTITY'];
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.ok(types.includes(type));
        if (payload && payload.displayName) {
          assert.deepEqual(payload, entityDetails.data[0]);
        }
      }
    };
    initializeEntityDetails({ entityId: '123' })(dispatch);
  });
});