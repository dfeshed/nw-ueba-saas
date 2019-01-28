import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  columns,
  isSchemaLoaded
} from 'investigate-files/reducers/schema/selectors';

module('Unit | selectors | schema');
const SCHEMA = Immutable.from({
  files: {
    schema: {
      schema: [
        {
          'name': 'entropy',
          'visible': false
        },
        {
          'name': 'firstFileName',
          'visible': true
        }
      ]

    }
  },
  preferences: {
    preferences: {
      filePreference: {
        visibleColumns: ['firstFileName']
      }
    }
  }
});

test('columns', function(assert) {
  const result = columns(SCHEMA);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 49, 'should return 48 columns + checkbox column');
  assert.equal(result[0].visible, true, 'entropy field is not visible');
  assert.equal(result[1].visible, false, 'firstFileName field is visible');
});

test('empty file preferences', function(assert) {
  const schema = Immutable.from({
    files: { schema: { schema: [
      {
        'name': 'entropy',
        'visible': false
      }] } },
    preferences: { preferences: { } }
  });
  const result = columns(schema);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 49, 'should return 48 columns + checkbox column');
  assert.equal(result[0].visible, false, 'entropy field is not visible');
});

test('isSchemaLoaded', function(assert) {

  assert.equal(isSchemaLoaded(SCHEMA), true, 'Schema loaded');

});

