import { module, test, skip } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  savedColumns,
  isSchemaLoaded,
  columns
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
          'name': 'machineCount',
          'visible': true
        }
      ]

    }
  },
  preferences: {
    preferences: {
      filePreference: {
        columnConfig: [{
          tableId: 'files',
          columns: [
            {
              field: 'machineCount',
              width: '7vw',
              displayIndex: 3
            }
          ]
        }]
      }
    }
  }
});

test('savedColumns', function(assert) {
  const result = savedColumns(SCHEMA);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 1, 'should return saved column config');
});

skip('empty file preferences', function(assert) {
  const schema = Immutable.from({
    files: { schema: { schema: [
      {
        'name': 'entropy',
        'visible': false
      }] } },
    preferences: { preferences: { } }
  });
  const result = savedColumns(schema);
  // length = total size + 1 checkbox column
  assert.equal(result.length, 49, 'should return 48 columns + checkbox column');
  assert.equal(result[0].visible, false, 'entropy field is not visible');
});

test('isSchemaLoaded', function(assert) {

  assert.equal(isSchemaLoaded(SCHEMA), true, 'Schema loaded');

});


test('columns will return the updated config with saved config', function(assert) {
  const savedColumnConfig = [{
    tableId: 'files',
    columns: [
      {
        field: 'firstSeenTime',
        width: '7vw',
        displayIndex: 4
      },
      {
        field: 'reputationStatus',
        width: '10vw',
        displayIndex: 5
      },
      {
        field: 'size',
        width: '3vw',
        displayIndex: 6
      }]
  }];
  const result = columns({
    preferences: {
      preferences: {
        filePreference: {
          columnConfig: savedColumnConfig
        }
      }
    }
  });
  assert.equal(result.length, 52, '52 visible columns.');
  assert.equal(result[3].preferredDisplayIndex, 6);
});

test('columns will return the default config', function(assert) {
  const result = columns({
    preferences: {
      preferences: {
        filePreference: {
          columnConfig: []
        }
      }
    }
  });
  assert.equal(result.length, 52, '5 visible columns.');
});
