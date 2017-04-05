import FilterQuery from 'respond/utils/filter-query';
import { SORT_TYPES_BY_NAME } from 'respond/utils/sort-types';
import { SINCE_WHEN_TYPES_BY_NAME } from 'respond/utils/since-when-types';
import { module, test } from 'qunit';

module('Unit | Utility | FilterQuery');

const defaultSort = [{
  field: SORT_TYPES_BY_NAME.SCORE_DESC.sortField,
  descending: true
}];

const defaultStream = {
  limit: 1000,
  batch: 100
};

test('An FilterQuery object can be created', function(assert) {
  const query = FilterQuery.create();
  assert.ok(query);
});

test('JSON structure is correct for FilterQuery object with default settings', function(assert) {
  const queryJson = FilterQuery.create().toJSON();
  const expectedJson = {
    filter: [],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(queryJson, expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling sortBy() properly changes the sort information in the json', function(assert) {
  const query = FilterQuery.create();
  const { sortField, isDescending } = SORT_TYPES_BY_NAME.STATUS_DESC;
  query.addSortBy(sortField, isDescending);
  const expectedJson = {
    filter: [],
    sort: [{
      field: SORT_TYPES_BY_NAME.STATUS_DESC.sortField,
      descending: SORT_TYPES_BY_NAME.STATUS_DESC.isDescending
    }],
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addFilter() properly adds the filter to the filters array', function(assert) {
  const query = FilterQuery.create();
  const field = 'status';
  const value = 'ASSIGNED';

  query.addFilter(field, value);
  const expectedJson = {
    filter: [
      {
        field,
        value
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addFilter() twice with the same field adds merges the values', function(assert) {
  const query = FilterQuery.create();
  const field = 'status';

  query.addFilter('status', 'ASSIGNED');
  query.addFilter('status', 'OLD');
  const expectedJson = {
    filter: [
      {
        field,
        values: ['ASSIGNED', 'OLD']
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addFilter() with either an empty (i.e., null or undefined) field or value adds no filter', function(assert) {
  const query = FilterQuery.create();

  query.addFilter(null, 'ASSIGNED')
      .addFilter('status')
      .addFilter('status', [null, undefined])
      .addFilter('status', []);

  const expectedJson = {
    filter: [],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addSinceWhenFilter() creates the proper range query', function(assert) {
  const query = FilterQuery.create();
  const field = 'modifiedDate';
  const from = query.addSinceWhenFilter(field, SINCE_WHEN_TYPES_BY_NAME.LAST_TWENTY_FOUR_HOURS, true);
  const expectedJson = {
    filter: [
      {
        field,
        range: {
          from,
          to: undefined
        }
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addSinceWhenFilter() twice removes the first filter and adds the second', function(assert) {
  const query = FilterQuery.create();
  const field = 'modifiedDate';
  query.addSinceWhenFilter(field, SINCE_WHEN_TYPES_BY_NAME.LAST_TWENTY_FOUR_HOURS);
  const from = query.addSinceWhenFilter(field, SINCE_WHEN_TYPES_BY_NAME.LAST_FORTY_EIGHT_HOURS, true);
  const expectedJson = {
    filter: [
      {
        field,
        range: {
          from,
          to: undefined
        }
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addRangeFilter() adds the proper range filter object', function(assert) {
  const query = FilterQuery.create();
  const field = 'riskScore';
  const from = 25;
  const to = 50;

  query.addRangeFilter(field, from, to);
  const expectedJson = {
    filter: [
      {
        field,
        range: {
          from,
          to
        }
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addHasAnyValueFilter() produces the expected query', function(assert) {
  const query = FilterQuery.create();
  const field = 'name';

  query.addHasAnyValueFilter(field);
  const expectedJson = {
    filter: [
      {
        field,
        isNull: false
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addHasNoValueFilter() produces the expected query', function(assert) {
  const query = FilterQuery.create();
  const field = 'name';

  query.addHasNoValueFilter(field);
  const expectedJson = {
    filter: [
      {
        field,
        isNull: true
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling removeFilter() produces the expected query', function(assert) {
  const query = FilterQuery.create()
    .addFilter('firstName', 'Matt')
    .addFilter('lastName', 'Meiske')
    .removeFilter('firstName');

  const expectedJson = {
    filter: [
      {
        field: 'lastName',
        value: 'Meiske'
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };
  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});

test('Calling addFilters() produces the expected query', function(assert) {
  const query = FilterQuery.create()
      .addFilters(['firstName', 'Ignatius'], ['lastName', 'Reilly'], ['hometown', 'New Orleans']);

  const expectedJson = {
    filter: [
      {
        field: 'firstName',
        value: 'Ignatius'
      },
      {
        field: 'lastName',
        value: 'Reilly'
      },
      {
        field: 'hometown',
        value: 'New Orleans'
      }
    ],
    sort: defaultSort,
    stream: defaultStream
  };

  assert.deepEqual(query.toJSON(), expectedJson, 'FilterQuery.toJSON() should return expected json when no additional filters are added');
});