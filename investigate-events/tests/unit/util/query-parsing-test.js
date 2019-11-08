import { module, test, skip } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import {
  convertTextToPillData,
  createFilter,
  createOperator,
  createParens,
  hasComplexText,
  isSearchTerm,
  parsePillDataFromUri,
  reassignTwinIds,
  transformTextToPillData,
  metaFiltersAsString,
  valueList
} from 'investigate-events/util/query-parsing';
import { DEFAULT_LANGUAGES, DEFAULT_ALIASES } from '../../helpers/redux-data-helper';
import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR,
  QUERY_FILTER,
  SEARCH_TERM_MARKER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

const { log } = console; // eslint-disable-line no-unused-vars

const params = {
  et: 0,
  eid: 1,
  mf: 'filename%20%3D%20\'reston%3D%5C\'virginia.sys\'',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3,
  pdhash: 'foo,bar,baz'
};

module('Unit | Util | Query Parsing', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('transformTextToPillData treats lack of operator as a complex query', function(assert) {
    const freeFormText = 'medium';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats bad meta as complex query', function(assert) {
    const freeFormText = "lakjsdlakjsd = 'yeah'";
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats operator that does not belong to meta as complex query', function(assert) {
    const freeFormText = 'sessionid contains 123';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats operator that requires value but does not have one as complex query', function(assert) {
    const freeFormText = 'medium =';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats operator that require no value but has one as complex query', function(assert) {
    const freeFormText = 'medium exists 10';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData handles when just meta and operator', function(assert) {
    const freeFormText = 'medium exists';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'medium', 'meta should match');
    assert.equal(result.operator, 'exists', 'operator should match');
    assert.equal(result.value, undefined, 'value should match');
  });

  test('transformTextToPillData returns pill data object', function(assert) {
    const freeFormText = 'medium = 1';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'medium', 'meta should match');
    assert.equal(result.operator, '=', 'operator should match');
    assert.equal(result.value, '1', 'value should match');
  });

  test('transformTextToPillData returns populated pill object even if operator embedded in value', function(assert) {
    const freeFormText = 'user.dst = \'1=2\'';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'user.dst', 'meta should match');
    assert.equal(result.operator, '=', 'operator should match');
    assert.equal(result.value, '\'1=2\'', 'value should match');
  });

  test('transformTextToPillData handles surrounding white space', function(assert) {
    const freeFormText = ' medium exists ';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'medium', 'meta should match');
    assert.equal(result.operator, 'exists', 'operator should match');
    assert.equal(result.value, undefined, 'value should match');
  });

  test('transformTextToPillData returns complex pill when forced to do so', function(assert) {
    const freeFormText = 'medium = foo';
    const result = transformTextToPillData(freeFormText, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, shouldForceComplex: true });
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, `(${freeFormText})`, 'complexFilterText should match');
  });

  test('transformTextToPillData returns text filter object because of Text filter marker', function(assert) {
    const text = `${SEARCH_TERM_MARKER}text${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, TEXT_FILTER, 'type should match');
    assert.equal(result.searchTerm, 'text', 'complexFilterText should match');
  });

  test('transformTextToPillData returns text filter even if it contains complex characters', function(assert) {
    const text = `${SEARCH_TERM_MARKER}(text)${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES });
    assert.equal(result.type, TEXT_FILTER, 'type should match');
    assert.equal(result.searchTerm, '(text)', 'complexFilterText should match');
  });

  test('transformTextToPillData returns multiple pills when flag is true', function(assert) {
    const text = 'medium = 3 AND b = \'google.com\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'value should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'b', 'meta should match');
    assert.equal(result[2].operator, '=', 'operator should match');
    assert.equal(result[2].value, '\'google.com\'', 'value should match');
  });

  test('transformTextToPillData returns multiple pills when using word form of AND operator', function(assert) {
    const text = 'medium = 3 AND b = \'google.com\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'value should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'b', 'meta should match');
    assert.equal(result[2].operator, '=', 'operator should match');
    assert.equal(result[2].value, '\'google.com\'', 'value should match');
  });

  test('transformTextToPillData returns query pills and text pills', function(assert) {
    const text = `medium = 3 AND ${SEARCH_TERM_MARKER}text filter${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'value should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, TEXT_FILTER, 'type should match');
    assert.equal(result[2].searchTerm, 'text filter', 'complexFilterText should match');
  });

  test('transformTextToPillData will not return more than one text filter', function(assert) {
    const text = `${SEARCH_TERM_MARKER}text${SEARCH_TERM_MARKER} AND ${SEARCH_TERM_MARKER}text 2${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, TEXT_FILTER, 'type should match');
    assert.equal(result[0].searchTerm, 'text', 'complexFilterText should match');
  });

  test('transformTextToPillData does not combine OR pills together', function(assert) {
    const text = 'b = \'google.com\' AND medium = 2 OR medium = 3';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 5);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'b', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'google.com\'', 'value should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'medium', 'meta should match');
    assert.equal(result[2].operator, '=', 'operator should match');
    assert.equal(result[2].value, '2', 'value should match');
    assert.equal(result[3].type, OPERATOR_OR, 'type should match');
    assert.equal(result[4].type, QUERY_FILTER, 'type should match');
    assert.equal(result[4].meta, 'medium', 'meta should match');
    assert.equal(result[4].operator, '=', 'operator should match');
    assert.equal(result[4].value, '3', 'value should match');
  });

  test('transformTextToPillData looks inside parenthesis', function(assert) {
    const text = '(b = \'google.com\') AND ((b = \'google.com\'))';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 9);
    assert.equal(result[0].type, OPEN_PAREN, 'pill should be paren');
    assert.equal(result[1].type, QUERY_FILTER, 'type should match');
    assert.equal(result[1].meta, 'b', 'meta should match');
    assert.equal(result[1].operator, '=', 'operator should match');
    assert.equal(result[1].value, '\'google.com\'', 'value should match');
    assert.equal(result[2].type, CLOSE_PAREN, 'pill should be paren');
    assert.equal(result[3].type, OPERATOR_AND, 'type should match');
    assert.equal(result[4].type, OPEN_PAREN, 'pill should be paren');
    assert.equal(result[5].type, OPEN_PAREN, 'pill should be paren');
    assert.equal(result[6].type, QUERY_FILTER, 'type should match');
    assert.equal(result[6].meta, 'b', 'meta should match');
    assert.equal(result[6].operator, '=', 'operator should match');
    assert.equal(result[6].value, '\'google.com\'', 'value should match');
    assert.equal(result[7].type, CLOSE_PAREN, 'pill should be paren');
    assert.equal(result[8].type, CLOSE_PAREN, 'pill should be paren');
  });

  test('transformTextToPillData makes an extra close paren a complex pill', function(assert) {
    const text = 'medium = 1)';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1', 'value should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'pill should be complex');
    assert.equal(result[2].complexFilterText, ')', 'text should be )');
  });

  test('transformTextToPillData makes an unterminated group a complex pill', function(assert) {
    const text = 'medium = 1 AND (medium = 2';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1', 'value should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'pill should be complex');
    assert.equal(result[2].complexFilterText, '(medium = 2', 'text should be )');
  });

  test('transformTextToPillData returns complex pill for non-indexed meta key', function(assert) {
    const text = 'ip.addr exists';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, 'ip.addr exists', 'complexFilterText should match');
  });

  test('transformTextToPillData returns an invalid pill for a mismatched type', function(assert) {
    const text = 'medium = \'foo\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'foo\'', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter an 8-bit Integer.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill when length is used with a negative number', function(assert) {
    const text = 'c length -3';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'c', 'meta should match');
    assert.equal(result[0].operator, 'length', 'operator should match');
    assert.equal(result[0].value, '-3', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter an integer greater than or equal to 1.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill when length is used with zero', function(assert) {
    const text = 'c length 0';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'c', 'meta should match');
    assert.equal(result[0].operator, 'length', 'operator should match');
    assert.equal(result[0].value, '0', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter an integer greater than or equal to 1.', 'validation error should be correct');
  });

  // Leaving here to remind me to implement this soon
  skip('transformTextToPillData returns an invalid pill for an out-of-range integer value', function(assert) {
    // bytes.src is UInt64, that number is 2^65
    const text = 'bytes.src = 36893488147419103000';

    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'bytes.src', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '36893488147419103000', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter a 64-bit Integer.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an valid pill when an alias is used correctly', function(assert) {
    const text = 'medium = \'Ethernet\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'Ethernet\'', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should not be invalid');
    assert.notOk(result[0].validationError, 'validation error should not exist');
  });

  test('transformTextToPillData returns an valid pill for a range using aliases', function(assert) {
    const text = 'ip.proto = \'TCP\' - \'UDP\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'ip.proto', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'TCP\'-\'UDP\'', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should not be invalid');
    assert.notOk(result[0].validationError, 'validation error should not exist');
  });

  test('transformTextToPillData returns an invalid pill for a range using an incorrect alias', function(assert) {
    const text = 'ip.proto = \'TCP\' - \'BAD\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'ip.proto', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'TCP\'-\'BAD\'', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.ok(result[0].validationError, 'validation error should exist');
  });

  test('transformTextToPillData returns an valid pill for a range using a normal value and an alias', function(assert) {
    const text = 'ip.proto = 1 - \'UDP\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'ip.proto', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1-\'UDP\'', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should not be invalid');
    assert.notOk(result[0].validationError, 'validation error should not exist');
  });

  test('transformTextToPillData returns an invalid pill for an invalid alias', function(assert) {
    const text = 'medium = \'IEEEthernet\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'IEEEthernet\'', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter an 8-bit Integer.', 'validation error should be correct');
  });

  test('transformTextToPillData returns a normal pill for an IPv4 address in CIDR notation', function(assert) {
    const text = 'alias.ip = 192.168.0.0/24';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ip', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '192.168.0.0/24', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should not be invalid');
    assert.notOk(result[0].validationError, 'pill should not have a validation error');
  });

  test('transformTextToPillData returns an invalid pill for an IP with a slash but no mask', function(assert) {
    const text = 'alias.ip = 192.168.0.1/';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ip', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '192.168.0.1/', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter a valid number following the forward slash.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for an IP with a slash but with alphanumeric mask', function(assert) {
    const text = 'alias.ip = 192.168.0.1/abc';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ip', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '192.168.0.1/abc', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter a valid number following the forward slash.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for an IP with an out-of-range mask', function(assert) {
    const text = 'alias.ip = 192.168.0.1/42';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ip', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '192.168.0.1/42', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'The CIDR mask must be between 0 and 32.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for an IP with a negative out-of-range mask', function(assert) {
    const text = 'alias.ip = 192.168.0.1/-5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ip', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '192.168.0.1/-5', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'Negative values are not allowed.', 'validation error should be correct');
  });

  test('transformTextToPillData returns a normal pill for an IPv6 address in CIDR notation', function(assert) {
    const text = 'alias.ipv6 = 3ffe:1900:4545:3:200:f8ff:fe21:67cf/64';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ipv6', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3ffe:1900:4545:3:200:f8ff:fe21:67cf/64', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should not be invalid');
    assert.notOk(result[0].validationError, 'pill should not have a validation error');
  });

  test('transformTextToPillData returns an invalid pill for an IPv6 with a slash but no mask', function(assert) {
    const text = 'alias.ipv6 = 3ffe:1900:4545:3:200:f8ff:fe21:67cf/';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ipv6', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3ffe:1900:4545:3:200:f8ff:fe21:67cf/', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter a valid number following the forward slash.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for an IPv6 with a slash but with alphanumeric mask', function(assert) {
    const text = 'alias.ipv6 = 3ffe:1900:4545:3:200:f8ff:fe21:67cf/abc';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ipv6', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3ffe:1900:4545:3:200:f8ff:fe21:67cf/abc', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter a valid number following the forward slash.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for an IPv6 with an out-of-range mask', function(assert) {
    const text = 'alias.ipv6 = 3ffe:1900:4545:3:200:f8ff:fe21:67cf/167';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ipv6', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3ffe:1900:4545:3:200:f8ff:fe21:67cf/167', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'The CIDR mask must be between 0 and 128.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for an IPv6 with a negative out-of-range mask', function(assert) {
    const text = 'alias.ipv6 = 3ffe:1900:4545:3:200:f8ff:fe21:67cf/-5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ipv6', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3ffe:1900:4545:3:200:f8ff:fe21:67cf/-5', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'Negative values are not allowed.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for negative numbers', function(assert) {
    const text = 'medium = -3';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '-3', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'Negative values are not allowed.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for upside-down ranges', function(assert) {
    const text = 'medium = 5-1';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '5-1', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'The second number in the range must be greater than the first.', 'validation error should be correct');
  });

  test('transformTextToPillData returns valid pill with l', function(assert) {
    const text = 'medium = l-5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, 'l-5', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns valid pill with L', function(assert) {
    const text = 'medium = L-5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, 'L-5', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns valid pill with l', function(assert) {
    const text = 'medium = 5-u';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '5-u', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns valid pill with L', function(assert) {
    const text = 'medium = 5-U';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '5-U', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns complex pill with l on the wrong side', function(assert) {
    const text = 'medium = 5-l';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
  });

  test('transformTextToPillData returns invalid pill with u on the wrong side', function(assert) {
    const text = 'medium = u-5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.ok(result[0].isInvalid, 'should be invalid');
  });

  test('transformTextToPillData returns pills with comma-separated values', function(assert) {
    const text = 'medium = 1,3,7,9';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1,3,7,9', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns an invalid pill for more than one comma in a row', function(assert) {
    const text = 'medium = 1,,3';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1,,3', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You cannot enter more than one comma in a row.', 'validation error should be correct');
  });

  test('transformTextToPillData returns an invalid pill for comma-separated values with a mismatched type', function(assert) {
    const text = 'medium = 1,2,\'string\'';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1,2,\'string\'', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter an 8-bit Integer.', 'validation error should be correct');
  });

  test('transformTextToPillData returns a complex pill for a non-value type included in a comma-separated list of values', function(assert) {
    const text = 'medium = 1,2,< AND b exists';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, 'medium = 1,2,<', 'complexFilterText should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'b', 'meta should match');
    assert.equal(result[2].operator, 'exists', 'operator should match');
    assert.notOk(result[2].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns a complex pill for a bonus trailing comma', function(assert) {
    const text = 'medium = 1,2, AND b exists';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, 'medium = 1,2,', 'complexFilterText should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'b', 'meta should match');
    assert.equal(result[2].operator, 'exists', 'operator should match');
    assert.notOk(result[2].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns a complex pill for a range without a second number', function(assert) {
    const text = 'medium = 3-';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, 'medium = 3-', 'complexFilterText should match');
  });

  test('transformTextToPillData returns a complex pill for expected meta but got nothing after open paren', function(assert) {
    const text = 'medium = 3 AND (';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'value should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, '(', 'complexFilterText should match');
  });

  test('transformTextToPillData returns a complex pill for expected meta but got something else', function(assert) {
    const text = 'medium = 3 AND 1337 AND medium = 5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 5);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'operator should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, '1337', 'complexFilterText should match');
    assert.equal(result[3].type, OPERATOR_AND, 'type should match');
    assert.equal(result[4].type, QUERY_FILTER, 'type should match');
    assert.equal(result[4].meta, 'medium', 'meta should match');
    assert.equal(result[4].operator, '=', 'operator should match');
    assert.equal(result[4].value, '5', 'value should match');
    assert.notOk(result[4].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns a complex pill for expected meta but got operator value', function(assert) {
    const text = 'medium = 3 AND = 4 AND medium = 5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 5);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'operator should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, '= 4', 'complexFilterText should match');
    assert.equal(result[3].type, OPERATOR_AND, 'type should match');
    assert.equal(result[4].type, QUERY_FILTER, 'type should match');
    assert.equal(result[4].meta, 'medium', 'meta should match');
    assert.equal(result[4].operator, '=', 'operator should match');
    assert.equal(result[4].value, '5', 'value should match');
    assert.notOk(result[4].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns a complex pill for expected operator but got meta then value', function(assert) {
    const text = 'medium = 3 AND medium equals 4 AND medium = 5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 5);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'operator should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, 'medium equals 4', 'complexFilterText should match');
    assert.equal(result[3].type, OPERATOR_AND, 'type should match');
    assert.equal(result[4].type, QUERY_FILTER, 'type should match');
    assert.equal(result[4].meta, 'medium', 'meta should match');
    assert.equal(result[4].operator, '=', 'operator should match');
    assert.equal(result[4].value, '5', 'value should match');
    assert.notOk(result[4].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns a complex pill for extra meta', function(assert) {
    const text = 'medium = 3 AND medium b = "a" AND medium = 5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 7);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'operator should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, 'medium', 'complexFilterText should match');
    assert.equal(result[3].type, OPERATOR_AND, 'type should match');
    assert.equal(result[4].type, QUERY_FILTER, 'type should match');
    assert.equal(result[4].meta, 'b', 'meta should match');
    assert.equal(result[4].operator, '=', 'operator should match');
    assert.equal(result[4].value, '\'a\'', 'value should match');
    assert.notOk(result[4].isInvalid, 'pill should be valid');
    assert.equal(result[5].type, OPERATOR_AND, 'type should match');
    assert.equal(result[6].type, QUERY_FILTER, 'type should match');
    assert.equal(result[6].meta, 'medium', 'meta should match');
    assert.equal(result[6].operator, '=', 'operator should match');
    assert.equal(result[6].value, '5', 'operator should match');
    assert.notOk(result[6].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns a complex pill for expected operator but got meta', function(assert) {
    // Intentional typo of "exists"
    const text = 'medium = 3 AND medium ecists AND medium = 5';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 5);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'operator should match');
    assert.notOk(result[0].isInvalid, 'pill should be valid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, 'medium ecists', 'complexFilterText should match');
    assert.equal(result[3].type, OPERATOR_AND, 'type should match');
    assert.equal(result[4].type, QUERY_FILTER, 'type should match');
    assert.equal(result[4].meta, 'medium', 'meta should match');
    assert.equal(result[4].operator, '=', 'operator should match');
    assert.equal(result[4].value, '5', 'value should match');
    assert.notOk(result[4].isInvalid, 'pill should be valid');
  });

  test('transformTextToPillData returns invalid pills alongside valid pills', function(assert) {
    const text = 'alias.ip = 8080 AND medium = 3';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ip', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '8080', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter an IPv4 address.', 'validation error should be correct');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].meta, 'medium', 'forward slash was not parsed correctly');
    assert.equal(result[2].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[2].value, '3', 'forward slash was not parsed correctly');
  });

  test('transformTextToPillData returns complex and normal pill for invalid meta key', function(assert) {
    // Test both the "bad" thing first, then the "good" thing first. Both should work as expected.
    const bad = "asdfasdfasdf = 'foobar'";
    const good = 'medium = 3';
    const badThenGood = `${bad} AND ${good}`;
    const goodThenBad = `${good} AND ${bad}`;

    const result1 = transformTextToPillData(badThenGood, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result1.length, 3);
    assert.equal(result1[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result1[0].complexFilterText, bad, 'complexFilterText should match');
    assert.equal(result1[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result1[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result1[2].meta, 'medium', 'meta should match');
    assert.equal(result1[2].operator, '=', 'operator should match');
    assert.equal(result1[2].value, '3', 'value should match');
    assert.notOk(result1[2].isInvalid, 'should not be invalid');

    const result2 = transformTextToPillData(goodThenBad, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result2.length, 3);
    assert.equal(result2[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result2[0].meta, 'medium', 'meta should match');
    assert.equal(result2[0].operator, '=', 'operator should match');
    assert.equal(result2[0].value, '3', 'value should match');
    assert.notOk(result2[0].isInvalid, 'should not be invalid');
    assert.equal(result2[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result2[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result2[2].complexFilterText, bad, 'complexFilterText should match');
  });

  test('transformTextToPillData returns complex and normal pill for invalid operator for meta', function(assert) {
    // Test both the "bad" thing first, then the "good" thing first. Both should work as expected.
    const bad = 'medium contains 3';
    const good = "b = 'get'";
    const badThenGood = `${bad} AND ${good}`;
    const goodThenBad = `${good} AND ${bad}`;

    const result1 = transformTextToPillData(badThenGood, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result1.length, 3);
    assert.equal(result1[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result1[0].complexFilterText, bad, 'complexFilterText should match');
    assert.equal(result1[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result1[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result1[2].meta, 'b', 'meta should match');
    assert.equal(result1[2].operator, '=', 'operator should match');
    assert.equal(result1[2].value, '\'get\'', 'value should match');
    assert.notOk(result1[2].isInvalid, 'should not be invalid');

    const result2 = transformTextToPillData(goodThenBad, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result2.length, 3);
    assert.equal(result2[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result2[0].meta, 'b', 'meta should match');
    assert.equal(result2[0].operator, '=', 'operator should match');
    assert.equal(result2[0].value, '\'get\'', 'value should match');
    assert.notOk(result2[0].isInvalid, 'should not be invalid');
    assert.equal(result2[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result2[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result2[2].complexFilterText, bad, 'complexFilterText should match');
  });

  test('transformTextToPillData returns complex and normal pill for invalid meta key w/ unary op', function(assert) {
    // Test both the "bad" thing first, then the "good" thing first. Both should work as expected.
    const bad = 'foobar exists';
    const good = 'medium = 3';
    const badThenGood = `${bad} AND ${good}`;
    const goodThenBad = `${good} AND ${bad}`;

    const result1 = transformTextToPillData(badThenGood, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result1.length, 3);
    assert.equal(result1[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result1[0].complexFilterText, bad, 'complexFilterText should match');
    assert.equal(result1[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result1[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result1[2].meta, 'medium', 'meta should match');
    assert.equal(result1[2].operator, '=', 'operator should match');
    assert.equal(result1[2].value, '3', 'value should match');
    assert.notOk(result1[2].isInvalid, 'should not be invalid');

    const result2 = transformTextToPillData(goodThenBad, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result2.length, 3);
    assert.equal(result2[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result2[0].meta, 'medium', 'meta should match');
    assert.equal(result2[0].operator, '=', 'operator should match');
    assert.equal(result2[0].value, '3', 'value should match');
    assert.notOk(result2[0].isInvalid, 'should not be invalid');
    assert.equal(result2[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result2[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result2[2].complexFilterText, bad, 'complexFilterText should match');
  });

  test('transformTextToPillData returns complex and normal pill for unary op w/ value', function(assert) {
    // Test both the "bad" thing first, then the "good" thing first. Both should work as expected.
    const bad = "b exists 'get'";
    const good = 'medium = 3';
    const badThenGood = `${bad} AND ${good}`;
    const goodThenBad = `${good} AND ${bad}`;

    const result1 = transformTextToPillData(badThenGood, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result1.length, 3);
    assert.equal(result1[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result1[0].complexFilterText, bad, 'complexFilterText should match');
    assert.equal(result1[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result1[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result1[2].meta, 'medium', 'meta should match');
    assert.equal(result1[2].operator, '=', 'operator should match');
    assert.equal(result1[2].value, '3', 'value should match');
    assert.notOk(result1[2].isInvalid, 'should not be invalid');

    const result2 = transformTextToPillData(goodThenBad, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result2.length, 3);
    assert.equal(result2[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result2[0].meta, 'medium', 'meta should match');
    assert.equal(result2[0].operator, '=', 'operator should match');
    assert.equal(result2[0].value, '3', 'value should match');
    assert.notOk(result2[0].isInvalid, 'should not be invalid');
    assert.equal(result2[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result2[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result2[2].complexFilterText, bad, 'complexFilterText should match');
  });

  test('transformTextToPillData returns complex and normal pill for unexpected tokens at end of clause after unary op', function(assert) {
    const text = 'b exists foobar 333';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'b', 'meta should match');
    assert.equal(result[0].operator, 'exists', 'operator should match');
    assert.notOk(result[0].isInvalid, 'should not be invalid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, 'foobar 333', 'complexFilterText should match');
  });

  test('transformTextToPillData returns complex and normal pill for unexpected tokens at end of clause', function(assert) {
    const text = "b = 'get' medium = 3";
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'b', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'get\'', 'operator should match');
    assert.notOk(result[0].isInvalid, 'should not be invalid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[2].complexFilterText, 'medium = 3', 'complexFilterText should match');
  });

  test('transformTextToPillData corrects bad alias capitalization', function(assert) {
    const text = "medium = 'ethernet'";
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'Ethernet\'', 'operator should match');
    assert.notOk(result[0].isInvalid, 'should not be invalid');
  });

  test('transformTextToPillData removes trailing AND/OR', function(assert) {
    const text = 'medium = 1 && medium = 2 &&';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1', 'operator should match');
    assert.notOk(result[0].isInvalid, 'should not be invalid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'medium', 'meta should match');
    assert.equal(result[2].operator, '=', 'operator should match');
    assert.equal(result[2].value, '2', 'operator should match');
    assert.notOk(result[2].isInvalid, 'should not be invalid');
  });

  test('transformTextToPillData removes trailing AND/OR', function(assert) {
    const text = 'medium = 1 && medium = 2 OR';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1', 'operator should match');
    assert.notOk(result[0].isInvalid, 'should not be invalid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'medium', 'meta should match');
    assert.equal(result[2].operator, '=', 'operator should match');
    assert.equal(result[2].value, '2', 'operator should match');
    assert.notOk(result[2].isInvalid, 'should not be invalid');
  });

  test('transformTextToPillData removes extra operators at beginning of query', function(assert) {
    const text = 'AND || medium = 1';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1', 'operator should match');
  });

  test('transformTextToPillData removes extra operators in the middle of a query', function(assert) {
    const text = 'medium = 1 AND OR medium = 2';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '1', 'operator should match');
    assert.notOk(result[0].isInvalid, 'should not be invalid');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'medium', 'meta should match');
    assert.equal(result[2].operator, '=', 'operator should match');
    assert.equal(result[2].value, '2', 'operator should match');
    assert.notOk(result[2].isInvalid, 'should not be invalid');
  });

  test('transformTextToPillData removes trailing AND/OR', function(assert) {
    const text = '(medium = 1 && medium = 2) &&';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 5);
    assert.equal(result[0].type, OPEN_PAREN, 'type should match');
    assert.equal(result[1].type, QUERY_FILTER, 'type should match');
    assert.equal(result[1].meta, 'medium', 'meta should match');
    assert.equal(result[1].operator, '=', 'operator should match');
    assert.equal(result[1].value, '1', 'operator should match');
    assert.notOk(result[1].isInvalid, 'should not be invalid');
    assert.equal(result[2].type, OPERATOR_AND, 'type should match');
    assert.equal(result[3].type, QUERY_FILTER, 'type should match');
    assert.equal(result[3].meta, 'medium', 'meta should match');
    assert.equal(result[3].operator, '=', 'operator should match');
    assert.equal(result[3].value, '2', 'operator should match');
    assert.notOk(result[3].isInvalid, 'should not be invalid');
    assert.equal(result[4].type, CLOSE_PAREN, 'type should match');
  });

  test('transformTextToPillData handles NOT', function(assert) {
    const text = 'NOT(medium = 1) AND medium = 2';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, 'NOT(medium = 1)', 'meta should match');
    assert.equal(result[1].type, OPERATOR_AND, 'type should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'medium', 'meta should match');
    assert.equal(result[2].operator, '=', 'operator should match');
    assert.equal(result[2].value, '2', 'operator should match');
    assert.notOk(result[2].isInvalid, 'should not be invalid');
  });

  test('transformTextToPillData handles NOT with missing right paren', function(assert) {
    const text = 'NOT(medium = 1 AND medium = 2';
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, 'NOT(medium = 1 AND medium = 2)', 'meta should match');
  });

  test('transformTextToPillData removes empty strings', function(assert) {
    const text = "b = 'get','','post'";
    const result = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'b', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'get\',\'post\'', 'operator should match');
    assert.notOk(result[0].isInvalid, 'should not be invalid');
  });

  test('parsePillDataFromUri correctly parses forward slashes and operators into pills', function(assert) {
    const result = parsePillDataFromUri(params.mf, DEFAULT_LANGUAGES);
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '\'reston=\\\'virginia.sys\'', 'forward slash was not parsed correctly');
  });

  test('parsePillDataFromUri correctly parses multiple params', function(assert) {
    const result = parsePillDataFromUri('filename%20%3D%20\'reston%3D%5C\'virginia.sys\' AND medium%20%3D%202', DEFAULT_LANGUAGES);
    assert.equal(result.length, 3, 'three pills came out');
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '\'reston=\\\'virginia.sys\'', 'forward slash was not parsed correctly');
    assert.equal(result[1].type, OPERATOR_AND, 'forward slash was not parsed correctly');
    assert.equal(result[2].meta, 'medium', 'forward slash was not parsed correctly');
    assert.equal(result[2].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[2].value, '2', 'forward slash was not parsed correctly');
  });

  test('parsePillDataFromUri correctly parses text filters with special chars', function(assert) {
    const result = parsePillDataFromUri('filename%20%3D%20\'reston%3D%5C\'virginia.sys\' AND ˸%foobar˸', DEFAULT_LANGUAGES);
    assert.equal(result.length, 3, 'three pills came out');
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '\'reston=\\\'virginia.sys\'', 'forward slash was not parsed correctly');
    assert.equal(result[1].type, OPERATOR_AND, 'forward slash was not parsed correctly');
    assert.equal(result[2].type, TEXT_FILTER, 'text filter should have been parsed properly');
    assert.equal(result[2].searchTerm, '%foobar', 'text filter search term was parsed properly');
  });

  test('metaFiltersAsString can convert a pill array to a string suitable for the metaFilter query param', function(assert) {
    const queryPill = transformTextToPillData('medium = 1', DEFAULT_LANGUAGES);
    const encQP = encodeURIComponent('medium = 1');
    const complexPill = transformTextToPillData('foo =', { language: DEFAULT_LANGUAGES });
    const encCP = encodeURIComponent('foo =');
    const AND = createOperator(OPERATOR_AND);
    const textPill = transformTextToPillData(`${SEARCH_TERM_MARKER}baz${SEARCH_TERM_MARKER}`, DEFAULT_LANGUAGES);
    const unencTP = `${SEARCH_TERM_MARKER}baz${SEARCH_TERM_MARKER}`; // Text Filters are not encoded
    const empty = transformTextToPillData('', DEFAULT_LANGUAGES);
    assert.equal(metaFiltersAsString([queryPill]), encQP, 'query pill only');
    assert.equal(metaFiltersAsString([complexPill]), encCP, 'complex pill only');
    assert.equal(metaFiltersAsString([textPill]), unencTP, 'text pill only');
    assert.equal(metaFiltersAsString([queryPill, AND, complexPill]), `${encQP} AND ${encCP}`, 'query and complex pills');
    assert.equal(metaFiltersAsString([queryPill, AND, textPill]), `${encQP} AND ${unencTP}`, 'query and text pills');
    assert.equal(metaFiltersAsString([complexPill, AND, textPill]), `${encCP} AND ${unencTP}`, 'complex and text pills');
    assert.equal(metaFiltersAsString([queryPill, AND, complexPill, AND, textPill]), `${encQP} AND ${encCP} AND ${unencTP}`, 'query, complex, and text pills');
    assert.deepEqual(metaFiltersAsString([empty]), undefined, 'empty pill');
    assert.equal(metaFiltersAsString([queryPill, empty]), `${encQP}`, 'query and empty pills');
    assert.equal(metaFiltersAsString([queryPill, AND, empty, textPill]), `${encQP} AND ${unencTP}`, 'query, empty, and text pills');
  });

  test('isSearchTerm is capable of determining if a string is marked as a Text pill', function(assert) {
    assert.ok(isSearchTerm(`${SEARCH_TERM_MARKER}foobar${SEARCH_TERM_MARKER}`));
    assert.notOk(isSearchTerm(`${SEARCH_TERM_MARKER}foobar`));
    assert.notOk(isSearchTerm(`foobar${SEARCH_TERM_MARKER}`));
    assert.notOk(isSearchTerm(`foo${SEARCH_TERM_MARKER}bar`));
    assert.notOk(isSearchTerm('foobar'));
  });

  test('createFilter can create a Query filter', function(assert) {
    const meta = 'foo';
    const operator = '=';
    const value = 'bar';
    const result = createFilter(QUERY_FILTER, meta, operator, value);
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, meta, 'meta should match');
    assert.equal(result.operator, operator, 'operator should match');
    assert.equal(result.value, value, 'value should match');
  });

  test('createFilter can create a Complex filter', function(assert) {
    const complexFilterText = '(foo bar)';
    const result = createFilter(COMPLEX_FILTER, complexFilterText);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, complexFilterText, 'complexFilterText should match');
  });

  test('createFilter can create a Text filter', function(assert) {
    const searchTerm = 'foo bar';
    const result = createFilter(TEXT_FILTER, searchTerm);
    assert.equal(result.type, TEXT_FILTER, 'type should match');
    assert.equal(result.searchTerm, searchTerm, 'complexFilterText should match');
  });

  test('hasComplexText can properly detect complex strings', function(assert) {
    // AND
    assert.ok(hasComplexText('&&'), 'Missed detecting "&&"');
    assert.ok(hasComplexText('x&&x'), 'Missed detecting "&&"');
    assert.ok(hasComplexText('AND'), 'Missed detecting "AND"');
    assert.ok(hasComplexText('xANDx'), 'Missed detecting "AND"');
    assert.notOk(hasComplexText('and'), 'Improperly detected "and" as complex');
    // OR
    assert.ok(hasComplexText('||'), 'Missed detecting "||"');
    assert.ok(hasComplexText('x||x'), 'Missed detecting "||"');
    assert.ok(hasComplexText('OR'), 'Missed detecting "OR"');
    assert.ok(hasComplexText('xORx'), 'Missed detecting "OR"');
    assert.notOk(hasComplexText('or'), 'Improperly detected "or" as complex');
    // NOT
    // assert.ok(hasComplexText('!'), 'Missed detecting "!"');
    // assert.ok(hasComplexText('x!x'), 'Missed detecting "!"');
    assert.ok(hasComplexText('NOT'), 'Missed detecting "NOT"');
    assert.ok(hasComplexText('xNOTx'), 'Missed detecting "NOT"');
    assert.notOk(hasComplexText('not'), 'Improperly detected "not" as complex');
    // PARENS
    assert.ok(hasComplexText('('), 'Missed detecting "("');
    assert.ok(hasComplexText('x(x'), 'Missed detecting "("');
    assert.ok(hasComplexText(')'), 'Missed detecting ")"');
    assert.ok(hasComplexText('x)x'), 'Missed detecting ")"');
  });

  test('convertTextToPillData parses half typed meta', function(assert) {
    const text = 'med';
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta: 'med' }, 'incorrect object returned');
  });

  test('convertTextToPillData treats an incorrect meta', function(assert) {
    const text = 'mediu boo';
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta: 'mediu boo' }, 'incorrect object returned');
  });

  test('convertTextToPillData ignores a space if correct meta is typed', function(assert) {
    const text = ' medium';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator: '' }, 'incorrect object returned');
  });

  test('convertTextToPillData throws if just spaces are sent out', function(assert) {
    const text = ' ';
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta: ' ' }, 'incorrect object returned');
  });

  test('convertTextToPillData maps a correctly entered meta', function(assert) {
    const text = 'medium';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator: '' }, 'incorrect object returned');
  });

  test('convertTextToPillData sends an empty operator string if no op is found', function(assert) {
    const text = 'medium ';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator: '' }, 'incorrect object returned');
  });

  test('convertTextToPillData does not map operator string if operator has more than one space in front', function(assert) {
    const text = 'medium  =';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator: ' =' }, 'incorrect object returned');
  });

  test('convertTextToPillData does not map operator string if operator is half typed', function(assert) {
    const text = 'medium !exi';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator: '!exi' }, 'incorrect object returned');
  });

  test('convertTextToPillData maps to operator if correctly typed in', function(assert) {
    const text = 'medium =';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const operator = {
      description: 'Equals',
      displayName: '=',
      hasValue: true,
      isExpensive: false
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator }, 'incorrect object returned');
  });

  test('convertTextToPillData maps to value if operator is properly mapped', function(assert) {
    const text = 'medium = 1';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const operator = {
      description: 'Equals',
      displayName: '=',
      hasValue: true,
      isExpensive: false
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator, value: '1' }, 'incorrect object returned');
  });

  test('convertTextToPillData maps to value if operator is properly mapped and value has multiple spaces', function(assert) {
    const text = 'medium =   1 ';
    const meta = {
      count: 0,
      displayName: 'Medium',
      flags: -2147482541,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: 'medium'
    };
    const operator = {
      description: 'Equals',
      displayName: '=',
      hasValue: true,
      isExpensive: false
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES });
    assert.deepEqual(result, { meta, operator, value: '  1' }, 'incorrect object returned');
  });

  test('_possibleMeta filters out isIndexedByNone meta', function(assert) {
    const randomNumber = Date.now();
    const text = `mediumtest${randomNumber} = 1`;
    const meta1 = {
      count: 0,
      displayName: 'Medium Test',
      flags: -2147483631,
      format: 'UInt8',
      formattedName: 'medium (Medium)',
      metaName: `mediumtest${randomNumber}`,
      isIndexedByKey: false,
      isIndexedByNone: true,
      isIndexedByValue: false
    };
    const DEFAULT_LANGUAGES2 = [
      ...DEFAULT_LANGUAGES,
      meta1
    ];
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES2 });
    assert.deepEqual(result, { meta: text }, 'incorrect object returned');
  });

  test('_possibleMeta does not filter out metaName sessionid', function(assert) {
    const randomStr = `${Date.now()}abc`;
    const text = `sessionid = ${randomStr}`;
    const meta = {
      count: 0,
      displayName: 'TEST',
      flags: -2147483631,
      format: 'UInt8',
      formattedName: 'TEST',
      metaName: 'sessionid',
      isIndexedByKey: false,
      isIndexedByNone: true,
      isIndexedByValue: false
    };
    const DEFAULT_LANGUAGES2 = [
      // remove all other meta options where metaName === 'sessionid'
      // to get correct output
      ...DEFAULT_LANGUAGES.filter((item) => item.metaName !== 'sessionid'),
      meta
    ];
    const operator = {
      description: 'Equals',
      displayName: '=',
      hasValue: true,
      isExpensive: false
    };
    const result = convertTextToPillData({ queryText: text, availableMeta: DEFAULT_LANGUAGES2 });
    assert.deepEqual(result, { meta, operator, value: randomStr }, 'incorrect object returned');
  });

  test('createParens creates an open and a close paren', function(assert) {
    const result = createParens();
    assert.ok(Array.isArray(result), 'should be an array');
    assert.equal(result.length, 2, 'should have 2 elements in the array');
    const [open, close] = result;
    assert.equal(open.type, OPEN_PAREN, 'wrong type, should be open paren');
    assert.equal(close.type, CLOSE_PAREN, 'wrong type, should be close paren');
    assert.ok(close.twinId !== undefined, 'missing twin id');
    assert.equal(close.twinId, open.twinId, 'twin ids do not match');
  });

  test('reassignTwinIds can reassign paren twinIds properly', function(assert) {
    // ┌─┬────┬─────────┬────┬─┬── original parens
    // ( ( QP ) ) AND ( ( QP ) )
    //          └──┴──┴─────────── inserted parens
    const filters = [
      { type: OPEN_PAREN, twinId: 'twinPill_1' },
      { type: OPEN_PAREN, twinId: 'twinPill_2' },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN, twinId: 'twinPill_2' },
      { type: CLOSE_PAREN, twinId: 'twinPill_4' }, // insertion point
      { type: OPERATOR_AND },
      { type: OPEN_PAREN, twinId: 'twinPill_4' },
      { type: OPEN_PAREN, twinId: 'twinPill_3' },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN, twinId: 'twinPill_3' },
      { type: CLOSE_PAREN, twinId: 'twinPill_1' }
    ];
    const insertionIndex = 4;
    const result = reassignTwinIds(filters, insertionIndex);
    // 1 2    2 4     4 3    3 1  original twinIds
    // ( ( QP ) ) AND ( ( QP ) )
    // 1 2    2 1     4 3    3 4  reassigned twinIds
    assert.ok(Array.isArray(result), 'should be an array');
    assert.equal(result[0].twinId, 'twinPill_1', 'item at index 0 incorrect');
    assert.equal(result[1].twinId, 'twinPill_2', 'item at index 1 incorrect');
    assert.equal(result[3].twinId, 'twinPill_2', 'item at index 3 incorrect');
    assert.equal(result[4].twinId, 'twinPill_1', 'item at index 4 incorrect');
    assert.equal(result[6].twinId, 'twinPill_4', 'item at index 6 incorrect');
    assert.equal(result[7].twinId, 'twinPill_3', 'item at index 7 incorrect');
    assert.equal(result[9].twinId, 'twinPill_3', 'item at index 9 incorrect');
    assert.equal(result[10].twinId, 'twinPill_4', 'item at index 10 incorrect');
  });

  test('valueList separates values into an array intelligently', function(assert) {
    // Splits up things into an array, strips quotes from individual items
    assert.deepEqual(valueList("1,'2',http,'smtp'"), [
      { value: '1', quoted: false },
      { value: '2', quoted: true },
      { value: 'http', quoted: false },
      { value: 'smtp', quoted: true }
    ]);
    assert.deepEqual(valueList('1,"2","http",smtp'), [
      { value: '1', quoted: false },
      { value: '2', quoted: true },
      { value: 'http', quoted: true },
      { value: 'smtp', quoted: false }
    ]);
    // Does not split on commas within quotes
    assert.deepEqual(valueList('7,\'8,9\',10'), [
      { value: '7', quoted: false },
      { value: '8,9', quoted: true },
      { value: '10', quoted: false }
    ]);
    // Does not end on escaped quotes
    assert.deepEqual(valueList('foo,bar,\'bazn\\\'t\''), [
      { value: 'foo', quoted: false },
      { value: 'bar', quoted: false },
      { value: 'bazn\\\'t', quoted: true }
    ]);
    // Remove escapes when told to
    assert.deepEqual(valueList('can,can\\\'t,could', { removeEscapes: true }), [
      { value: 'can', quoted: false },
      { value: 'can\'t', quoted: false },
      { value: 'could', quoted: false }
    ]);
    // Skip past escapes and don't let them end a string early
    assert.deepEqual(valueList('action\\\' sample\\\''), [
      { value: 'action\\\' sample\\\'', quoted: false }
    ]);
    assert.deepEqual(valueList('action\\\' sample\\\'', { removeEscapes: true }), [
      { value: 'action\' sample\'', quoted: false }
    ]);
    // Empty string is empty array
    assert.deepEqual(valueList(''), []);
    // Empty quotes is empty array
    assert.deepEqual(valueList('\'\''), []);
    assert.deepEqual(valueList('""'), []);
    // Spaces should be empty
    assert.deepEqual(valueList('   '), []);
    assert.deepEqual(valueList('\'   \''), []);
    assert.deepEqual(valueList('"   "'), []);
    // Quotes with padding should be empty
    assert.deepEqual(valueList('  \'\'  '), []);
    assert.deepEqual(valueList('  ""  '), []);
    assert.deepEqual(valueList('  \'   \'  '), []);
    assert.deepEqual(valueList('  "   "  '), []);
  });

  test('createOperator can create an AND operator', function(assert) {
    const result = createOperator(OPERATOR_AND);
    assert.equal(result.type, OPERATOR_AND, 'type should match');
  });

  test('createOperator can create an OR operator', function(assert) {
    const result = createOperator(OPERATOR_OR);
    assert.equal(result.type, OPERATOR_OR, 'type should match');
  });
});
