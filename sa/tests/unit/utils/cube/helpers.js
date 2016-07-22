/**
 * @file Cube test helpers.
 * Utilities for unit testing for Cube.
 * @public
 */
import Cube from 'sa/utils/cube/base';

/**
 * Compares the length of a given array, and its values, with a given length & set of values.
 * Calls a given assert method with a given errMessage to record all results.
 * @param {[*]} arr The array to be tested.
 * @param {number} [length] The expected length of the array. If null, length is not tested.
 * @param {string} [field] The field whose values will be read for testing. If null, tests are skipped.
 * @param {Object} [values] Map of expected values; maps an array index to an expected value. If null,
 * tests are skipped.
 * @param {function} assert The assert function to be invoked with the test results.
 * @param {string} [errMessage] Option error msg to be appended to messages that are passed to assert.
 * @public
 */
export function arrayCompare(arr, length, field, values, assert, errMessage) {
  if (!assert) {
    return;
  }
  errMessage = errMessage || '';

  // Compare the array length.
  if (length !== null) {
    assert.equal(arr && arr.length, length, `Unexpected results size.${errMessage}`);
  }

  // Compare the values at given indices of the array.
  if (arr && field && values) {
    for (let k in values) {
      if (values.hasOwnProperty(k)) {
        let idx = parseInt(k, 10);
        let found = arr[idx] && arr[idx][field];
        let expected = values[idx];
        assert.equal(found, expected, `Unexpected value in result at index: ${idx}${errMessage}`);
      }
    }
  }
}

/**
 * Compares the results of calling a given cube's field's includes() method with a given set of values against
 * a given set of expected results.
 * @param {Object} cube The cube to be tested.
 * @param {String} field The cube field whose filtering is to be tested.
 * @param {Object} values Map of expected values; maps an input argument for filter.includes() to an expected return value.
 * @param {function} assert The assert function to be invoked with the test results.
 * @param {string} [errMessage] Option error msg to be appended to messages that are passed to assert.
 * @public
 */
export function includesCompare(cube, field, values, assert, errMessage) {
  if (!cube || !assert || !field || !values) {
    return;
  }
  errMessage = errMessage || '';

  let filter = cube.get('fields')[field].get('filter');
  assert.ok(filter, `Unable to create filter for cube field ${field}`);
  for (let j in values) {
    if (values.hasOwnProperty(j)) {
      let val = isNaN(Number(j)) ? j : Number(j);
      assert.equal(
        filter.includes(val),
        values[j],
        `Unexpected filter.includes() for: + ${val} . ${errMessage}`
      );
    }
  }
}

/**
 * Testing the results of a sort operation on a given field for a given set of records.
 * Assumes the records have a field that tell what the resulting index of each item should be after the sort.
 * @param {string} sortField The field to sort the given records by.
 * @param {string} orderField The field that specifies the index of the record after the records have been sorted
 * ascending by the given sortField.
 * @param {Object} [fieldsCfg] The fields config for the cube. Use this to specify if the sortField is of type default,
 * array or csv.  If null, the field is assumed to be of type default.
 * @param {[*]} records The data records to be used for populating the cube.
 * @param {function} assert The assert function to be invoked with the test results.
 * @param {string} [errMessage] Option error msg to be appended to messages that are passed to assert.
 * @public
 */
export function testSorting(sortField, orderField, fieldsCfg, records, assert, errMessage) {
  let len = records && records.length;
  if (!len || !assert) {
    return;
  }
  errMessage = errMessage || '';

  function checkOrder(arr, field, descending) {
    for (let i = 0; i < len; i++) {
      assert.equal(
          arr[i] && arr[i][field],
          descending ? len - i - 1 : i,
          `Unexpected sort order at index: ${i}.${errMessage}`
      );
    }
  }

  // Instantiate the object and populate data.
  let obj = Cube.create({ fieldsConfig: fieldsCfg });
  assert.ok(obj, 'Unable to create object.');
  obj.get('records').pushObjects(records);

  obj.sort(sortField, true);
  checkOrder(obj.get('results'), orderField, true, 'Testing descending order.');

  obj.sort(sortField, false);
  checkOrder(obj.get('results'), orderField, false, 'Testing ascending order.');
}

/**
 * Compares the results and the expected results of a grouping operation for a given field of a given cube.
 * @public
 */
export function testGrouping(cube, field, expectedGroups, errMessage, assert) {
  let foundGroups = cube.get('fields')[field].get('groups');
  assert.ok(foundGroups, `Unable to fetch groups for field${ field}.${errMessage}`);

  if (expectedGroups) {
    for (let i = 0, len = (foundGroups && foundGroups.length) || 0; i < len; i++) {
      assert.equal(foundGroups[i].value, expectedGroups[foundGroups[i].key], errMessage);
    }
  }
}

/**
 * Compares the length of a given array, and its values, with a given length & set of values.
 * Also calls a field's filter.includes() with a given set of values and compares the results.
 * @public
 */
export function checkCubeResults(obj, size, indexField, indices, includeField, includes, assert, errMessage) {
  arrayCompare(obj.get('results'), size, indexField, indices, assert, errMessage);
  includesCompare(obj, includeField || indexField, includes, assert, errMessage);
}

export default {
  arrayCompare,
  includesCompare,
  testSorting,
  testGrouping,
  checkCubeResults
};
