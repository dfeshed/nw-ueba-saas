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
          'name': 'machineIdentity.agentVersion',
          'visible': false
        }
      ]
    }
  },
  preferences: {
    preferences: {
      machinePreference: {
        columnConfig: [{
          tableId: 'hosts',
          columns: [
            { field: 'machineIdentity.machineOsType', displayIndex: 3 }
          ]
        }]
      }
    }
  }
});
test('getHostTableColumns', function(assert) {
  const result = getHostTableColumns(SCHEMA);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 61, 'should return 61 columns including checkbox column');
  // 0th field is a checkbox.
  assert.equal(result[3].field, 'agentStatus.lastSeenTime', 'Machine OS Type field is visible');
  assert.equal(result[3].visible, false, 'Machine OS Type field is visible');
  assert.equal(result[12].field, 'machineIdentity.agent.driverErrorCode', 'Agent version is 12th child in the config');
  assert.equal(result[12].visible, false, 'Agent Version field is not visible');
});

test('getHostTableColumns returns the default columns', function(assert) {
  const result = getHostTableColumns({ preferences: { preferences: {} } });
  // length = total size + 1 checkbox column
  assert.equal(result.length, 61, 'should return 61 columns including checkbox column');
  // 0th field is a checkbox.
  assert.equal(result[3].field, 'agentStatus.lastSeenTime', 'Machine OS Type field is visible');
  assert.equal(result[3].visible, true, 'Machine OS Type field is visible');
  assert.equal(result[12].field, 'machineIdentity.agent.driverErrorCode', 'Agent version is 12th child in the config');
  assert.equal(result[12].visible, true, 'Agent Version field is not visible');
});


test('displayIndex for non-visible columns being set', function(assert) {
  const result = getHostTableColumns(SCHEMA);
  const newIndex = result[4].preferredDisplayIndex;
  assert.equal(newIndex, 5, 'agentStatus.lastSeenTime, has index more than, number of items in visible columns');
});


test('prepareSchema', function(assert) {
  const result = prepareSchema(SCHEMA);
  assert.equal(result.length, 58, 'should return 58 columns ');
  assert.equal(result[0].title, 'investigateHosts.hosts.column.agentStatus.lastSeenTime', 'should return the added title property');
});


