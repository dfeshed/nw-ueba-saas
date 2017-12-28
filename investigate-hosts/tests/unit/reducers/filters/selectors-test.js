import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
    searchableColumns,
    filters,
    appliedFilters,
    listWithoutDefault,
    isValidExpression
} from 'investigate-hosts/reducers/filters/selectors';

module('Unit | selectors | filters');

const STATE = Immutable.from({
  endpoint: {
    filter: {
      schemas: [{
        name: 'id',
        description: 'Agent Id',
        dataType: 'STRING',
        searchable: true,
        defaultProjection: false,
        wrapperType: 'STRING'
      },
      {
        name: 'machine.agentVersion',
        description: 'Agent Version',
        dataType: 'STRING',
        searchable: true,
        defaultProjection: false,
        wrapperType: 'STRING'
      },
      {
        name: 'machine.machineOsType',
        description: 'Operating System',
        dataType: 'STRING',
        values: [
          'windows',
          'linux',
          'mac'
        ],
        searchable: true,
        defaultProjection: true,
        wrapperType: 'STRING'
      }],
      expressionList: [
        {
          propertyName: 'machine.machineOsType',
          propertyValues: [
            {
              value: 'windows'
            }
          ],
          restrictionType: 'IN'
        },
        {
          restrictionType: 'IN',
          propertyName: 'machine.agentVersion',
          propertyValues: null
        }
      ],
      filterSelected: {
        createdOn: 0,
        lastModifiedOn: 0,
        id: 'all',
        name: 'All Hosts',
        filterType: 'MACHINE',
        systemFilter: true
      }
    }
  }
});

test('searchableColumns', function(assert) {
  const result = searchableColumns(STATE);
  assert.equal(result.length, 3, 'should give size 3 if searchable columns present');
});
test('filters', function(assert) {
  const result = filters(STATE);
  assert.equal(result.length, 3, 'filters result should be 3');
});
test('appliedFilters', function(assert) {
  const result = appliedFilters(STATE);
  assert.equal(result.length, 2, 'applied filters should be 2');
});
test('listWithoutDefault', function(assert) {
  const result = listWithoutDefault(STATE);
  assert.equal(result.length, 3, 'should be 3, as there are no defaults');
});
test('isValidExpression true', function(assert) {
  const result = isValidExpression(STATE);
  assert.equal(result, true, 'valid expression');
});

test('isValidExpression false', function(assert) {
  const state = Immutable.from({
    endpoint: {
      schema: {
        schema: []
      },
      filter: { }
    }
  });
  const result = isValidExpression(state);
  assert.equal(result, false, 'invalid expression');
});

