import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  getHostTableColumns,
  prepareSchema,
  preferencesConfig
} from 'investigate-hosts/reducers/schema/selectors';

module('Unit | selectors | schema');
const SCHEMA = Immutable.from({
  endpoint: {
    schema: {
      schema: [
        {
          'name': 'id',
          'description': 'Agent Id',
          'dataType': 'STRING',
          'searchable': true,
          'defaultProjection': true,
          'wrapperType': 'STRING'
        },
        {
          'name': 'machine.agentVersion',
          'description': 'Agent Version',
          'dataType': 'STRING',
          'searchable': true,
          'defaultProjection': true,
          'wrapperType': 'STRING'
        }
      ],
      visibleColumns: ['machine.agentVersion']
    }
  }
});
test('getHostTableColumns', function(assert) {
  const result = getHostTableColumns(SCHEMA);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 3, 'should return 3 columns including checkbox column');
  // 0th field is a checkbox. visibleColumns overrides defaultProjection
  assert.equal(result[1].visible, false, 'Agent Id field is not visible');
  assert.equal(result[2].visible, true, 'Agent Version field is visible');
});


test('prepareSchema', function(assert) {
  const result = prepareSchema(SCHEMA);
  assert.equal(result.length, 2, 'should return 2 columns ');
  assert.equal(result[0].title, 'investigateHosts.hosts.column.id', 'should return the added title property');
});

test('preferenceConfig', function(assert) {
  const result = preferencesConfig(SCHEMA);
  assert.equal(result.items[0].options.length, 2, '2 options are set from columns');
});
