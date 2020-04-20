import { module, test } from 'qunit';
import buildExplorerQuery from 'respond/actions/api/util/explorer-build-query';

module('Unit | Utility | Explorer Build Query');

test('it produces a query whose json includes the specified filter and sort', function(assert) {
  const query = buildExplorerQuery({ 'alert.type': 'File Share' }, { sortField: 'created' }, 'created');
  assert.deepEqual(query.toJSON(),
    {
      filter: [{
        field: 'alert.type',
        value: 'File Share'
      }],
      sort: [
        {
          descending: true,
          field: 'created'
        }
      ],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
});

// Standard filters are transformed from key/value to { filter: key, value: value }, but if a filter contains the property
// isNull, it is not transformed, but instead the whole value is used as a filter
test('an isNull filter value is appended to the query filters as-is', function(assert) {
  const query = buildExplorerQuery({
    'alert.type': 'File Share',
    'assignee': {
      field: 'assignee',
      isNull: true
    }
  }, { sortField: 'created' }, 'created');
  assert.deepEqual(query.toJSON(),
    {
      filter: [{
        field: 'alert.type',
        value: 'File Share'
      }, {
        field: 'assignee',
        isNull: true
      }],
      sort: [
        {
          descending: true,
          field: 'created'
        }
      ],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
});

test('a filter with a field that is the same as the defaultDateFilterField and has a "start" property produces a custom range query', function(assert) {
  const query = buildExplorerQuery({
    myDate: {
      start: 0,
      end: 10000000000
    }
  }, { sortField: 'myDate' }, 'myDate');
  assert.deepEqual(query.toJSON(),
    {
      filter: [{
        field: 'myDate',
        range: {
          from: 0,
          to: 10000000000,
          type: 'date'
        }
      }],
      sort: [
        {
          descending: true,
          field: 'myDate'
        }
      ],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
});

test('a filter that has an "isRange" property that is true is transformed into a standard range query', function(assert) {
  const query = buildExplorerQuery({
    severity: {
      isRange: true,
      type: 'numeric',
      start: 30,
      end: 80
    }
  }, { sortField: 'myDate' }, 'myDate');
  assert.deepEqual(query.toJSON(),
    {
      filter: [{
        field: 'severity',
        range: {
          from: 30,
          to: 80,
          type: 'numeric'
        }
      }],
      sort: [
        {
          descending: true,
          field: 'myDate'
        }
      ],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
});