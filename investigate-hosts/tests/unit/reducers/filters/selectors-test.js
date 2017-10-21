import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
    searchableColumns,
    filters,
    appliedFilters,
    listWithoutDefault
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
  assert.equal(result.length, 0, ' should be one');
});
