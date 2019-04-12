import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import {
  createFilter,
  isSearchTerm,
  parsePillDataFromUri,
  transformTextToPillData,
  uriEncodeMetaFilters
} from 'investigate-events/util/query-parsing';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';
import {
  COMPLEX_FILTER,
  QUERY_FILTER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

const { log } = console; // eslint-disable-line no-unused-vars

const params = {
  et: 0,
  eid: 1,
  mf: 'filename%20%3D%20<reston%3D\'virginia.sys>',
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

  test('transformTextToPillData returns complex filter object because of ||', function(assert) {
    const freeFormText = 'medium = 1 || medium = 32';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: `(${freeFormText})`
    }, 'properties should match');
    assert.equal(result.toString(), `(${freeFormText})`, 'should have correct string representation');
  });

  test('transformTextToPillData treats lack of operator as a complex query', function(assert) {
    const freeFormText = 'medium';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: freeFormText
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData treats bad meta as complex query', function(assert) {
    const freeFormText = 'lakjsdlakjsd = yeah';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: freeFormText
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData treats operator that does not belong to meta as complex query', function(assert) {
    const freeFormText = 'sessionid contains 123';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: freeFormText
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData treats operator that requires value but does not have one as complex query', function(assert) {
    const freeFormText = 'medium =';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: freeFormText
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData treats operator that require no value but has one as complex query', function(assert) {
    const freeFormText = 'medium exists 10';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: freeFormText
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData handles when just meta and operator', function(assert) {
    const freeFormText = 'medium exists';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: QUERY_FILTER,
      meta: 'medium',
      operator: 'exists',
      value: undefined
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData returns pill data object', function(assert) {
    const freeFormText = 'medium = 1';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: QUERY_FILTER,
      meta: 'medium',
      operator: '=',
      value: '1'
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData returns populated pill object even if operator embedded in value', function(assert) {
    const freeFormText = 'user.dst = \'1=2\'';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: QUERY_FILTER,
      meta: 'user.dst',
      operator: '=',
      value: '\'1=2\''
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData handles surrounding white space', function(assert) {
    const freeFormText = ' medium exists ';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: QUERY_FILTER,
      meta: 'medium',
      operator: 'exists',
      value: undefined
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText.trim(), 'should have correct string representation');
  });

  test('transformTextToPillData treats operator that has extra text as complex query', function(assert) {
    const freeFormText = 'medium =foo';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: freeFormText
    }, 'properties should match');
    assert.equal(result.toString(), freeFormText, 'should have correct string representation');
  });

  test('transformTextToPillData returns complex pill when forced to do so', function(assert) {
    const freeFormText = 'medium = foo';
    const shouldForceComplex = true;
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES, shouldForceComplex);
    assert.propEqual(result, {
      type: COMPLEX_FILTER,
      complexFilterText: '(medium = foo)'
    }, 'properties should match');
    assert.equal(result.toString(), '(medium = foo)', 'should have correct string representation');
  });

  test('transformTextToPillData returns text filter object because of Text filter marker', function(assert) {
    const text = '~some random text';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: TEXT_FILTER,
      searchTerm: text
    }, 'properties should match');
    assert.equal(result.toString(), text, 'should have correct string representation');
  });

  test('transformTextToPillData returns text filter even if it contains complex characters', function(assert) {
    const text = '~(some random text)';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES);
    assert.propEqual(result, {
      type: TEXT_FILTER,
      searchTerm: text
    }, 'properties should match');
  });

  test('parsePillDataFromUri correctly parses forward slashes and operators into pills', function(assert) {
    const result = parsePillDataFromUri(params.mf, DEFAULT_LANGUAGES);
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '<reston=\'virginia.sys>', 'forward slash was not parsed correctly');
  });

  test('parsePillDataFromUri correctly parses multiple params', function(assert) {
    const result = parsePillDataFromUri('filename%20%3D%20<reston%3D\'virginia.sys>/medium%20%3D%20foo', DEFAULT_LANGUAGES);
    assert.equal(result.length, 2, 'two pills came out');
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '<reston=\'virginia.sys>', 'forward slash was not parsed correctly');
    assert.equal(result[1].meta, 'medium', 'forward slash was not parsed correctly');
    assert.equal(result[1].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[1].value, 'foo', 'forward slash was not parsed correctly');
  });

  test('uriEncodeMetaFilters can convert a pill array to a string suitable for the metaFilter query param', function(assert) {
    const queryPill = transformTextToPillData('medium = 1', DEFAULT_LANGUAGES);
    const encQP = encodeURIComponent('medium = 1');
    const complexPill = transformTextToPillData('(bar)', DEFAULT_LANGUAGES);
    const encCP = encodeURIComponent('(bar)');
    const textPill = transformTextToPillData('~baz', DEFAULT_LANGUAGES);
    const encTP = encodeURIComponent('~baz');
    const empty = transformTextToPillData('', DEFAULT_LANGUAGES);
    assert.equal(uriEncodeMetaFilters([queryPill]), encQP, 'query pill only');
    assert.equal(uriEncodeMetaFilters([complexPill]), encCP, 'complex pill only');
    assert.equal(uriEncodeMetaFilters([textPill]), encTP, 'text pill only');
    assert.equal(uriEncodeMetaFilters([queryPill, complexPill]), `${encQP}/${encCP}`, 'query and complex pills');
    assert.equal(uriEncodeMetaFilters([queryPill, textPill]), `${encQP}/${encTP}`, 'query and text pills');
    assert.equal(uriEncodeMetaFilters([complexPill, textPill]), `${encCP}/${encTP}`, 'complex and text pills');
    assert.equal(uriEncodeMetaFilters([queryPill, complexPill, textPill]), `${encQP}/${encCP}/${encTP}`, 'query, complex, and text pills');
    assert.deepEqual(uriEncodeMetaFilters([empty]), undefined, 'empty pill');
    assert.equal(uriEncodeMetaFilters([queryPill, empty]), `${encQP}`, 'query and empty pills');
    assert.equal(uriEncodeMetaFilters([queryPill, empty, textPill]), `${encQP}/${encTP}`, 'query, empty, and text pills');
  });

  test('isSearchTerm is capable of determining if a string is marked as a Text pill', function(assert) {
    assert.ok(isSearchTerm('~foobar'));
    assert.notOk(isSearchTerm('foobar'));
    assert.notOk(isSearchTerm('foo~bar'));
  });

  test('createFilter can create a Query filter', function(assert) {
    const meta = 'foo';
    const operator = '=';
    const value = 'bar';
    const queryFilter = createFilter(QUERY_FILTER, meta, operator, value);
    assert.propEqual(queryFilter, {
      type: QUERY_FILTER,
      meta,
      operator,
      value
    }, 'properties should match');
  });

  test('createFilter can create a Complex filter', function(assert) {
    const complexFilterText = '(foo bar)';
    const complexFilter = createFilter(COMPLEX_FILTER, complexFilterText);
    assert.propEqual(complexFilter, {
      type: COMPLEX_FILTER,
      complexFilterText
    }, 'properties should match');
  });

  test('createFilter can create a Text filter', function(assert) {
    const searchTerm = 'foo bar';
    const textFilter = createFilter(TEXT_FILTER, searchTerm);
    assert.propEqual(textFilter, {
      type: TEXT_FILTER,
      searchTerm
    }, 'properties should match');
  });
});