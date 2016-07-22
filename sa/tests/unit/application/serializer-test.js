import { moduleFor, test } from 'ember-qunit';
import Ember from 'ember';

const { getOwner } = Ember;

moduleFor('serializer:application', 'Unit | Serializer | application', {
  // Specify the other units that are required for this test.
  // Our serializer takes store as an input param, so we need the store service.
  // Our store, in turn, uses websockets to support streaming, so we need the websocket service too.
  // For an alternative approach using integration tests rather than unit tests,
  // @see http://stackoverflow.com/questions/29982856/how-to-create-unit-tests-for-ember-js-adapter-serializers
  needs: ['service:store', 'service:websocket']
});

test('it exists', function(assert) {
  let serializer = this.subject();
  assert.ok(serializer);
});

test('Serializer normalizes correctly for a payload with basic single object', function(assert) {
  assert.expect(1);

  const modelName = 'foo';

  const inputHash = {
    data: {
      id: 1,
      prop1: 'val1',
      prop2: 'val2'
    }
  };

  const expectedOutputHash = {
    data: {
      type: modelName,
      id: 1,
      attributes: {
        prop1: 'val1',
        prop2: 'val2'
      }
    }
  };

  let store = getOwner(this).lookup('service:store');
  let result = this.subject().normalizeResponse(store, { modelName }, inputHash);

  assert.deepEqual(result, expectedOutputHash, 'Unexpected result.');
});

test('Serializer normalizes correctly for a payload with an array of two objects', function(assert) {
  assert.expect(1);

  const modelName = 'foo';

  const inputHash = {
    data: [
      {
        id: 1,
        prop1: 'val1',
        prop2: 'val2'
      }, {
        id: 2,
        prop3: 'val3',
        prop4: 'val4'
      }
    ]
  };

  const expectedOutputHash = {
    data: [
      {
        type: modelName,
        id: 1,
        attributes: {
          prop1: 'val1',
          prop2: 'val2'
        }
      }, {
        type: modelName,
        id: 2,
        attributes: {
          prop3: 'val3',
          prop4: 'val4'
        }
      }
    ]
  };

  let store = getOwner(this).lookup('service:store');
  let result = this.subject().normalizeResponse(store, { modelName }, inputHash);

  assert.deepEqual(result, expectedOutputHash, 'Unexpected result.');
});
