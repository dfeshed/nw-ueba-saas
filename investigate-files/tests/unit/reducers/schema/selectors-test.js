import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  columns,
  preferenceConfig
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
      ],
      visibleColumns: ['firstFileName']
    }
  }
});
test('columns', function(assert) {
  const result = columns(SCHEMA);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 2, 'should return 2 columns + checkbox column');
  assert.equal(result[0].visible, false, 'entropy field is not visible');
  assert.equal(result[1].visible, true, 'firstFileName field is visible');
});

test('preferenceConfig', function(assert) {
  const result = preferenceConfig(SCHEMA);
  assert.equal(result.items[0].options.length, 2, '2 options are set from columns');
});

