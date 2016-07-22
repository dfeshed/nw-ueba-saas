import Ember from 'ember';
import { module, test } from 'qunit';
import Cube from 'sa/utils/cube/base';
import ENUM_FIELD_TYPE from 'sa/utils/cube/field/enum-type';
import helpers from './helpers';

const { run } = Ember;

module('Unit | Utility | cube/base');

let ARRAY_TYPE_RECORDS = [
  { id: 0, countries: ['USA'], sortOrder: 2 },
  { id: 1, countries: ['Canada'], sortOrder: 0 },
  { id: 2, countries: ['Korea'], sortOrder: 1 },
  { id: 3, countries: ['USA', 'Canada'], sortOrder: 3 },
  { id: 4, countries: ['USA', 'Korea'], sortOrder: 4 }
];
let CSV_TYPE_RECORDS = [
  { id: 0, countries: 'USA', sortOrder: 2 },
  { id: 1, countries: 'Canada', sortOrder: 0 },
  { id: 2, countries: 'Korea', sortOrder: 1 },
  { id: 3, countries: 'USA, Canada', sortOrder: 3 },
  { id: 4, countries: 'USA, Korea', sortOrder: 4 }
];

test('it exists, stores records, and maintains filters', function(assert) {

  // Instantiate a cube and confirm its data size.
  let obj = Cube.create({
    fieldsConfig: {
      id: {
        type: ENUM_FIELD_TYPE.DEFAULT
      }
    }
  });
  assert.ok(obj, 'Unable to create cube instance.');
  assert.equal(obj.get('records').length, 0, 'Unexpected data size after instantiation.');

  // Add more data and recheck data size.
  obj.get('records').pushObjects([{ id: 1 }, { id: 2 }]);
  assert.equal(obj.get('records').length, 2, 'Unexpected data size after adding data.');

  // Next, before clearing the data, apply a filter. We will test if this filter is preserved after clearing data.
  obj.filter([{ field: 'id', value: 1 }], true);
  assert.equal(obj.get('fields.id.filter').includes(1), true, 'Unexpected includes result from simple filter.');

  // Now clear the data, and confirm that data is cleared and filter is preserved.
  run(function() {
    obj.get('records').clear();
  });
  assert.equal(obj.get('records').length, 0, 'Unexpected data size after clearing data.');
  assert.equal(obj.get('fields.id.filter').includes(1), true, 'Unexpected includes result from simple filter.');

  run(function() {
    obj.destroy();
  });
});

test('it sorts fields of type default', function(assert) {
  helpers.testSorting(
    'name',
    'sortOrder',
    null,
    [
      { name: 'Canada', sortOrder: 0 },
      { name: 'USA', sortOrder: 2 },
      { name: 'Korea', sortOrder: 1 }
    ],
    assert,
    'Testing a text sort field.'
  );
  helpers.testSorting(
    'count',
    'sortOrder',
    null,
    [
      { count: 1000, sortOrder: 2 },
      { count: 100, sortOrder: 0 },
      { count: 500, sortOrder: 1 }
    ],
    assert,
    'Testing a numeric sort field.'
  );
});

test('it sorts fields of type array', function(assert) {
  helpers.testSorting(
    'countries',
    'sortOrder',
    { countries: { type: ENUM_FIELD_TYPE.ARRAY } },
    ARRAY_TYPE_RECORDS,
    assert,
    'Testing an array of strings.'
  );
});

test('it sorts fields of type csv', function(assert) {
  helpers.testSorting(
    'countries',
    'sortOrder',
    { countries: { type: ENUM_FIELD_TYPE.CSV } },
    CSV_TYPE_RECORDS,
    assert,
    'Testing an array of strings.'
  );
});

test('it resorts its results after a record has been edited', function(assert) {
  assert.expect(9);

  let array = [
      { myid: 'z', name: 'Z' },
      { myid: 'a', name: 'A' },
      { myid: 'j', name: 'J' }
  ];
  let obj = Cube.create({
    fieldsConfig: {
      myid: {
        type: ENUM_FIELD_TYPE.DEFAULT
      },
      name: {
        type: ENUM_FIELD_TYPE.DEFAULT
      }
    },
    idField: 'myid',
    sortField: 'name',
    sortDesc: false,
    array
  });
  let results = obj.get('results');

  // Check that results are initially sorted as expected.
  assert.equal(results[0].myid, 'a', 'Unexpected sort result.');
  assert.equal(results[1].myid, 'j', 'Unexpected sort result.');
  assert.equal(results[2].myid, 'z', 'Unexpected sort result.');

  // Edit the data, this time using the id-signature for the API.
  array.edit('a', { name: 'YY' });

  // Check that results have been re-sorted.
  results = obj.get('results');
  assert.equal(results[0].myid, 'j', 'Unexpected sort result after editing with an id signature.');
  assert.equal(results[1].myid, 'a', 'Unexpected sort result after editing with an id signature.');
  assert.equal(results[2].myid, 'z', 'Unexpected sort result after editing with an id signature.');

  // Now edit the data using the KVO-compliant API.
  array.edit(array[0], { name: 'A' });

  // Check that results have been re-sorted.
  results = obj.get('results');

  assert.equal(results[0].myid, 'z', 'Unexpected sort result after editing with an object signature.');
  assert.equal(results[1].myid, 'j', 'Unexpected sort result after editing with an object signature.');
  assert.equal(results[2].myid, 'a', 'Unexpected sort result after editing with an object signature.');
});

// Creates a cube instance, feeds it given data records, then applies a variety of filters for a field
// whose values are simple numerical primitives.
test('it filters fields of default type', function(assert) {

  // Instantiate the object.
  let obj = Cube.create();
  assert.ok(obj, 'Unable to create object.');

  // Feed it data and tell it how to sort the data so we can predict the results of these operations.
  let FIELD_KEY = 'field1';
  obj.get('records').pushObjects([
    { field1: 1 },
    { field1: 2 },
    { field1: 10 },
    { field1: 100 },
    { field1: 1000 }
  ]);
  obj.sort(FIELD_KEY, true);

  // Confirm sorted results.
  helpers.checkCubeResults(
    obj,
    5,
    FIELD_KEY,
    { 0: 1000, 4: 1 },
    null,
    { 1: true, 1000: true },
    assert,
    'Testing sort results.'
  );

  // Filter by a single value.
  obj.filter(FIELD_KEY, 2);
  helpers.checkCubeResults(
    obj,
    1,
    FIELD_KEY,
    { 0: 2 },
    null,
    { 2: true, 1: false },
    assert,
    'Testing results when filtering by a single value.'
  );

  // Add a second value to the filter.
  obj.filter(FIELD_KEY, 1000, { add: true });
  helpers.checkCubeResults(
    obj,
    2,
    FIELD_KEY,
    { 0: 1000, 1: 2 },
    null,
    { 1000: true, 10: false, 2: true },
    assert,
    'Testing results after adding a second value to the filter.'
  );

  // Add a duplicate value to the filter; confirm that it changes nothing.
  obj.filter(FIELD_KEY, 2, { add: true });
  helpers.checkCubeResults(
    obj,
    2,
    FIELD_KEY,
    { 0: 1000, 1: 2 },
    null,
    { 1000: true, 1: false, 2: true },
    assert,
    'Testing results after adding a duplicate value to the filter.'
  );

  // Try removing a single value from the filter.
  obj.filter(FIELD_KEY, 2, { remove: true });
  helpers.checkCubeResults(
    obj,
    1,
    FIELD_KEY,
    { 0: 1000 },
    null,
    { 1000: true, 2: false },
    assert,
    'Testing results after removing a single value from the filter.'
  );

  // Test clearing the filter by resetting it to null.
  obj.filter(FIELD_KEY, null);
  helpers.checkCubeResults(
    obj,
    5,
    FIELD_KEY,
    { 0: 1000, 4: 1 },
    null,
    { 0: true, 2: true },
    assert,
    'Testing results with null filter.'
  );

  // Test filtering by a range of values.
  obj.filter(FIELD_KEY, { from: 2, to: 10 });
  helpers.checkCubeResults(
    obj,
    1,
    FIELD_KEY,
    { 0: 2 },
    null,
    { 1: false, 2: true, 3: true, 10: false },
    assert,
    'Testings results with a range filter.'
  );

  // Test concatenating ranges of filter values.
  obj.filter(FIELD_KEY, { from: 10, to: 101 }, { add: true });
  obj.filter(FIELD_KEY, { from: 1, to: 2 }, { add: true });
  helpers.checkCubeResults(
    obj,
    4,
    FIELD_KEY,
    { 0: 100, 3: 1 },
    null,
    { 0: false, 1: true, 2: true, 50: true, 101: false },
    assert,
    'Testing results after concatenating contiguous ranges of filter values.'
  );

  // Confirm that concatenating two non-contiguous ranges is not supported, and acts as a reset operation.
  obj.filter(FIELD_KEY, { from: 900, to: 1100 }, { add: true });
  helpers.checkCubeResults(
    obj,
    1,
    FIELD_KEY,
    { 0: 1000 },
    null,
    { 1: false, 900: true, 1000: true, 1101: false },
    assert,
    'Testing results after concatenating non-contiguous ranges of filter values.'
  );

  // Test filtering by a list of values.
  obj.filter(FIELD_KEY, [10, 100]);
  helpers.checkCubeResults(
    obj,
    2,
    FIELD_KEY,
    { 0: 100, 1: 10 },
    null,
    { 1: false, 10: true, 50: false, 100: true, 1000: false },
    assert,
    'Testing results with a list of filter values.'
  );

  // Test adding a single value to the list filter.
  obj.filter(FIELD_KEY, 2, { add: true });
  helpers.checkCubeResults(
    obj,
    3,
    FIELD_KEY,
    { 0: 100, 1: 10, 2: 2 },
    null,
    { 2: true, 5: false, 10: true, 100: true },
    assert,
    'Testing results after adding to a list of filter values.'
  );

  // Test removing a single value from the list filter.
  obj.filter(FIELD_KEY, 10, { remove: true });
  helpers.checkCubeResults(
    obj,
    2,
    FIELD_KEY,
    { 0: 100, 1: 2 },
    null,
    { 2: true, 10: false, 100: true },
    assert,
    'Testing results after removing a value from a list of filter values.'
  );

  // Test filtering by a filter function.
  obj.filter(FIELD_KEY, function(d) {
    return (d % 10) === 0;
  });
  helpers.checkCubeResults(
    obj,
    3,
    FIELD_KEY,
    { 0: 1000, 1: 100, 2: 10 },
    null,
    { 2: false, 1: false, 25: false, 10000: true },
    assert,
    'Testing results with a filter function.'
  );

  // Confirm that adding two filter functions is not supported, and supports in a reset operation.
  obj.filter(FIELD_KEY, function(d) {
    return d === 1;
  }, { add: true });
  helpers.checkCubeResults(
    obj,
    1,
    { 0: 1 },
    { 0: false, 1: true, 10: false },
    assert,
    'Testing after adding two filter functions together.'
  );
});

// Helper method for testing fields whose values are not default primitive single-value types (e.g., Arrays, CSV strings).
function testFilteringByNonDefaultField(indexField, filterField, fieldsCfg, records, assert) {

  // Instantiate the object.
  let obj = Cube.create({ fieldsConfig: fieldsCfg });
  assert.ok(obj, 'Unable to create object.');

  // Feed it data and tell it how to sort the data so we can predict the results of these operations.
  obj.get('records').pushObjects(records);
  obj.sort(indexField, true);

  // Confirm sorted results.
  helpers.checkCubeResults(
    obj,
    5,
    indexField,
    { 0: 4, 1: 3, 2: 2, 3: 1, 4: 0 },
    filterField,
    { 'Any': true },
    assert,
    'Testing sort results.'
  );

  // Filter by a single value.
  obj.filter(filterField, 'Canada');
  helpers.checkCubeResults(
    obj,
    2,
    indexField,
    { 0: 3, 1: 1 },
    filterField,
    { Canada: true, USA: false },
    assert,
    'Testing with single value filter.'
  );

  // Confirm that adding the same single value to the filter does nothing.
  obj.filter(filterField, 'Canada', { add: true });
  helpers.checkCubeResults(
    obj,
    2,
    indexField,
    { 0: 3, 1: 1 },
    filterField,
    { Canada: true, USA: false },
    assert,
    'Testing after adding a duplicate single value filter.'
  );

  // Test adding another single value to the filter.
  obj.filter(filterField, 'USA', { add: true });
  helpers.checkCubeResults(
    obj,
    4,
    indexField,
    { 0: 4, 1: 3, 2: 1, 3: 0 },
    filterField,
    { Canada: true, USA: true, Korea: false },
    assert,
    'Testing after adding a second single value filter.'
  );

  // Test removing a single value from the filter.
  obj.filter(filterField, 'Canada', { remove: true });
  helpers.checkCubeResults(
    obj,
    3,
    indexField,
    { 0: 4, 1: 3, 2: 0 },
    filterField,
    { Canada: false, USA: true, Korea: false },
    assert,
    'Testing after removing a single value filter.'
  );

  // Test clearing the filter by resetting it to null.
  obj.filter(filterField, null);
  helpers.checkCubeResults(
    obj,
    5,
    indexField,
    { 0: 4, 1: 3, 2: 2, 3: 1, 4: 0 },
    filterField,
    { Any: true },
    assert,
    'Testing after resetting filter to null.'
  );

  // Test filtering by a range of values.
  obj.filter(filterField, { from: 'A', to: 'M' });
  helpers.checkCubeResults(
    obj,
    4,
    indexField,
    { 0: 4, 1: 3, 2: 2, 3: 1 },
    filterField,
    { Canada: true, India: true, Nicaragua: false, USA: false },
    assert,
    'Testing with a range filter.'
  );

  // Test concatenating a contiguous range of values.
  obj.filter(filterField, { from: 'M', to: 'V' }, { add: true });
  helpers.checkCubeResults(
    obj,
    5,
    indexField,
    { 0: 4, 1: 3, 2: 2, 3: 1, 4: 0 },
    filterField,
    { Canada: true, India: true, Nicaragua: true, USA: true, Venezuela: false },
    assert,
    'Testing with a concatenated contiguous range filter.'
  );

  // Test concatenating a non-contiguous range of values, and confirm that it results in a reset.
  obj.filter(filterField, { from: 'Y', to: 'Z' }, { add: true });
  helpers.checkCubeResults(
    obj,
    0,
    indexField,
    null,
    filterField,
    { Canada: false, USA: false, Korea: false, Yemen: true },
    assert,
    'Testing with a concatenated non-contiguous range filter.'
  );

  // Test filtering by a list of values.
  obj.filter(filterField, ['Korea', 'Canada']);
  helpers.checkCubeResults(
    obj,
    4,
    indexField,
    { 0: 4, 1: 3, 2: 2, 3: 1 },
    filterField,
    { Korea: true, Canada: true, USA: false, Any: false },
    assert,
    'Testing with a list filter.'
  );

  // Test filtering by a filter function.
  obj.filter(filterField, function(d) {
    return d.indexOf('o') > -1;
  });
  helpers.checkCubeResults(
    obj,
    2,
    indexField,
    { 0: 4, 1: 2 },
    filterField,
    { Korea: true, Canada: false, USA: false, Romania: true, India: false },
    assert,
    'Testing with filter function.'
  );
}

// Creates a cube instance, feeds it given data records, then applies a variety of filters for a field
// whose values are Arrays of strings.
test('it filters fields of array type', function(assert) {
  testFilteringByNonDefaultField(
    'id',
    'countries',
    {
      'countries': {
        type: ENUM_FIELD_TYPE.ARRAY
      }
    },
    ARRAY_TYPE_RECORDS,
    assert
  );
});

// Creates a cube instance, feeds it given data records, then applies a variety of filters for a field
// whose values are comma-separated-value strings.
test('it filters fields of csv type', function(assert) {
  testFilteringByNonDefaultField(
    'id',
    'countries',
    {
      'countries': {
        type: ENUM_FIELD_TYPE.CSV
      }
    },
    CSV_TYPE_RECORDS,
    assert
  );
});

// Creates a cube instance, feeds it given data records, then applies grouping and tests the results.
test('it groups fields of default type', function(assert) {

  // Initialize the cube.
  let FIELD_KEY = 'field1';
  let obj = Cube.create({
    fieldsConfig: {
      field1: {
        dataType: 'number'
      }
    },
    sortBy: FIELD_KEY
  });

  assert.ok(obj, 'Unable to create object.');
  obj.get('records').pushObjects([
    { field1: 10 },
    { field1: 20 },
    { field1: 30 },
    { field1: 10 },
    { field1: 30 }
  ]);

  // Perform a simple grouping operation.
  helpers.testGrouping(
    obj,
    FIELD_KEY,
    {
      10: 2,
      20: 1,
      30: 2
    },
    'Unexpected results in single value grouping.',
    assert
  );

  // Try adding a record and confirm that the grouping result was updated.
  obj.get('records').pushObjects([{ field1: 30 }]);
  helpers.testGrouping(
    obj,
    FIELD_KEY,
    {
      10: 2,
      20: 1,
      30: 3
    },
    'Unexpected results in single value grouping after adding a record.',
    assert
  );
});

// Helper function for testing group operations on a field of non-default type.
function testGroupingByNonDefaultField(fieldsCfg, records, groupField, recordsToAdd, expected, expectedAfterAdd, assert) {

  // Initialize the cube.
  let obj = Cube.create({
    fieldsConfig: fieldsCfg,
    sortBy: groupField
  });
  assert.ok(obj, 'Unable to create object.');
  obj.get('records').pushObjects(records);

  // Perform a simple grouping operation.
  helpers.testGrouping(
    obj,
    groupField,
    expected,
    'Unexpected results in single value grouping.',
    assert
  );

  // Try adding a record and confirm that the grouping result was updated.
  obj.get('records').pushObjects(recordsToAdd);
  helpers.testGrouping(
    obj,
    groupField,
    expectedAfterAdd,
    'Unexpected results in single value grouping after adding a record.',
    assert
  );
}

// Creates a cube instance, feeds it given data records, then applies grouping and tests the results.
test('it groups fields of Array type', function(assert) {
  testGroupingByNonDefaultField(
    {
      countries: {
        type: ENUM_FIELD_TYPE.ARRAY
      }
    },
    ARRAY_TYPE_RECORDS,
    'countries',
    [{ id: 6, countries: ['Korea', 'Canada'] }],
    {
      'USA': 3,
      'Canada': 2,
      'Korea': 2
    },
    {
      'USA': 3,
      'Canada': 3,
      'Korea': 3
    },
    assert
  );
});

// Creates a cube instance, feeds it given data records, then applies grouping and tests the results.
test('it groups fields of CSV type', function(assert) {
  testGroupingByNonDefaultField(
    {
      countries: {
        type: ENUM_FIELD_TYPE.CSV
      }
    },
    CSV_TYPE_RECORDS,
    'countries',
    [{ id: 6, countries: 'Korea, Canada' }],
    {
      'USA': 3,
      'Canada': 2,
      'Korea': 2
    },
    {
      'USA': 3,
      'Canada': 3,
      'Korea': 3
    },
    assert
  );
});
