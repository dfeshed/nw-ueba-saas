import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  getHostTableColumns,
  prepareSchema
} from 'investigate-hosts/reducers/schema/selectors';

module('Unit | selectors | schema');
const SCHEMA = Immutable.from({
  endpoint: {
    schema: {
      schema: [
        {
          'name': 'id',
          'visible': true
        },
        {
          'name': 'machine.agentVersion',
          'visible': false
        }
      ]
    }
  },
  preferences: {
    preferences: {}
  }
});
test('getHostTableColumns', function(assert) {
  const result = getHostTableColumns(SCHEMA);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 5, 'should return 4 columns including checkbox column');
  // 0th field is a checkbox.
  assert.equal(result[1].visible, true, 'Agent Id field is not visible');
  assert.equal(result[3].visible, false, 'Agent Version field is visible');
});


test('prepareSchema', function(assert) {
  const result = prepareSchema(SCHEMA);
  assert.equal(result.length, 2, 'should return 2 columns ');
  assert.equal(result[0].title, 'investigateHosts.hosts.column.id', 'should return the added title property');
});


