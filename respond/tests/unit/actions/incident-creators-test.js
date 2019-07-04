import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as Creators from 'respond/actions/creators/incidents-creators';
import * as ACTION_TYPES from 'respond/actions/types';
import { patchSocket } from '../../helpers/patch-socket';

module('Unit | Actions | Incident | Creators', function(hooks) {
  setupTest(hooks);

  test('test addRelatedIndicatorsToIncident', async function(assert) {
    assert.expect(6);
    const dispatchDone = assert.async();

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'updateRecord', 'promiseRequest method is correct');
      assert.equal(modelName, 'associated-alerts', 'promiseRequest modelName is correct');
      assert.deepEqual(query.data, {
        associated: ['a-1', 'a-2'],
        entity: {
          id: 'INC-123'
        }
      });
    });

    const mockGetState = () => ({
      respond: {
        incident: {
          searchResults: ['foo']
        }
      }
    });

    const mockDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.ADD_RELATED_INDICATORS, 'action type is correct');
      action.promise.then(({ response, searchResults }) => {
        assert.deepEqual(searchResults, ['foo'], 'searchResults is as expected');
        assert.equal(response.code, 0, 'response is as expected');
        dispatchDone();
      });
    };

    const thunk = Creators.addRelatedIndicatorsToIncident(['a-1', 'a-2'], 'INC-123');


    thunk(mockDispatch, mockGetState);
  });
});
