import { module, test } from 'qunit';
import { addSortBy, addFilter } from 'configure/actions/utils/query-util';

module('Unit | Util | Query Util', function() {

  test('Add Sort By to the query', function(assert) {
    const query = {};
    const result = addSortBy(query, 'name', true);
    assert.deepEqual(result.sort, { keys: ['name'], descending: true });
  });

  test('Add Filter By to the query', function(assert) {
    const query = {};
    const expressionList = [
      {
        propertyName: 'test',
        propertyValues: [{ value: 'test' }]
      }
    ];
    const result = addFilter(query, expressionList);
    assert.deepEqual(result.criteria.expressionList.length, 1);
  });
});
