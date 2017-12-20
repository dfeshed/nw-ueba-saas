import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  searchableColumns,
  filters,
  appliedFilters,
  listWithoutDefault,
  isValidExpression
} from 'investigate-files/reducers/filter/selectors';

module('Unit | selectors | filters');

const STATE = Immutable.from({
  files: {
    schema: {
      schema: [{
        name: 'firstFileName',
        description: 'firstFileName',
        dataType: 'STRING',
        searchable: true,
        defaultProjection: false,
        wrapperType: 'STRING'
      },
      {
        name: 'format',
        description: 'format',
        dataType: 'STRING',
        searchable: true,
        defaultProjection: false,
        wrapperType: 'STRING'
      }]
    },
    filter: {
      expressionList: [
        {
          propertyName: 'firstFileName',
          propertyValues: [
            {
              value: 'windows'
            }
          ],
          restrictionType: 'IN'
        },
        {
          restrictionType: 'IN',
          propertyName: 'format',
          propertyValues: null
        }
      ],
      lastFilterAdded: {
        createdOn: 0,
        lastModifiedOn: 0,
        id: 'all',
        name: 'Windows',
        filterType: 'FILE',
        isSystemFilter: true
      }
    }
  }
});

test('searchableColumns', function(assert) {
  const result = searchableColumns(STATE);
  assert.equal(result.length, 2, 'should give size 2 if searchable columns present');
});
test('filters', function(assert) {
  const result = filters(STATE);
  assert.equal(result.length, 2, 'filters result should be 2');
});
test('appliedFilters', function(assert) {
  const result = appliedFilters(STATE);
  assert.equal(result.length, 2, 'applied filters should be 2');
});
test('listWithoutDefault', function(assert) {
  const result = listWithoutDefault(STATE);
  assert.equal(result.length, 2, ' should be 2 as there are no defaults');
});

test('isValidExpression true', function(assert) {
  const result = isValidExpression(STATE);
  assert.equal(result, true, 'valid expression');
});

test('isValidExpression false', function(assert) {
  const state = Immutable.from({
    files: {
      schema: {
        schema: []
      },
      filter: { }
    }
  });
  const result = isValidExpression(state);
  assert.equal(result, false, 'invalid expression');
});
