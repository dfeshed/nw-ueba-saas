import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  columns
} from 'investigate-files/reducers/schema/selectors';

module('Unit | selectors | schema');
const SCHEMA = Immutable.from({
  files: {
    schema: {
      schema: [
        {
          'name': 'entropy',
          'dataType': 'DOUBLE',
          'searchable': true,
          'defaultProjection': false,
          'wrapperType': 'NUMBER'
        },
        {
          'name': 'firstFileName',
          'dataType': 'STRING',
          'searchable': true,
          'defaultProjection': true,
          'wrapperType': 'STRING'
        }
      ]
    }
  }
});
test('columns', function(assert) {
  const result = columns(SCHEMA);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 2, 'should return 2 columns including checkbox column');
});

