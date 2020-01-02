import { module, test } from 'qunit';
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
          'name': 'hostCount',
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
              field: 'hostCount',
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

test('empty file preferences', function(assert) {
  const schema = Immutable.from({
    files: { schema: { schema: [
      {
        'name': 'entropy',
        'visible': false
      }] } },
    preferences: { preferences: { } }
  });
  const result = savedColumns(schema);
  assert.equal(result.length, 0, 'should return 0 columns + checkbox column');
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
  assert.equal(result.length, 50, '50 visible columns.');
  assert.equal(result[1].field, 'firstFileName');
  assert.equal(result[2].field, 'score');
  assert.equal(result[3].preferredDisplayIndex, 4);
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
  assert.equal(result.length, 50, '50 visible columns.');
  assert.equal(result[1].field, 'firstFileName');
  assert.equal(result[2].field, 'score');
});
