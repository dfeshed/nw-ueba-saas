import { module, test, skip } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import {
  createFilter,
  hasComplexText,
  isSearchTerm,
  parsePillDataFromUri,
  transformTextToPillData,
  uriEncodeMetaFilters,
  convertTextToPillData
} from 'investigate-events/util/query-parsing';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';
import {
  COMPLEX_FILTER,
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

  test('transformTextToPillData returns complex filter object because of ||', function(assert) {
    const freeFormText = 'medium = 1 || medium = 32';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, `(${freeFormText})`, 'complexFilterText should match');
  });

  test('transformTextToPillData treats lack of operator as a complex query', function(assert) {
    const freeFormText = 'medium';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats bad meta as complex query', function(assert) {
    const freeFormText = 'lakjsdlakjsd = "yeah"';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats operator that does not belong to meta as complex query', function(assert) {
    const freeFormText = 'sessionid contains 123';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats operator that requires value but does not have one as complex query', function(assert) {
    const freeFormText = 'medium =';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData treats operator that require no value but has one as complex query', function(assert) {
    const freeFormText = 'medium exists 10';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, freeFormText, 'complexFilterText should match');
  });

  test('transformTextToPillData handles when just meta and operator', function(assert) {
    const freeFormText = 'medium exists';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'medium', 'meta should match');
    assert.equal(result.operator, 'exists', 'operator should match');
    assert.equal(result.value, undefined, 'value should match');
  });

  test('transformTextToPillData returns pill data object', function(assert) {
    const freeFormText = 'medium = 1';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'medium', 'meta should match');
    assert.equal(result.operator, '=', 'operator should match');
    assert.equal(result.value, '1', 'value should match');
  });

  test('transformTextToPillData returns populated pill object even if operator embedded in value', function(assert) {
    const freeFormText = 'user.dst = \'1=2\'';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'user.dst', 'meta should match');
    assert.equal(result.operator, '=', 'operator should match');
    assert.equal(result.value, '\'1=2\'', 'value should match');
  });

  test('transformTextToPillData handles surrounding white space', function(assert) {
    const freeFormText = ' medium exists ';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.equal(result.type, QUERY_FILTER, 'type should match');
    assert.equal(result.meta, 'medium', 'meta should match');
    assert.equal(result.operator, 'exists', 'operator should match');
    assert.equal(result.value, undefined, 'value should match');
  });

  test('transformTextToPillData returns complex pill when forced to do so', function(assert) {
    const freeFormText = 'medium = foo';
    const shouldForceComplex = true;
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES, shouldForceComplex);
    assert.equal(result.type, COMPLEX_FILTER, 'type should match');
    assert.equal(result.complexFilterText, `(${freeFormText})`, 'complexFilterText should match');
  });

  test('transformTextToPillData returns text filter object because of Text filter marker', function(assert) {
    const text = `${SEARCH_TERM_MARKER}text${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES);
    assert.equal(result.type, TEXT_FILTER, 'type should match');
    assert.equal(result.searchTerm, 'text', 'complexFilterText should match');
  });

  test('transformTextToPillData returns text filter even if it contains complex characters', function(assert) {
    const text = `${SEARCH_TERM_MARKER}(text)${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES);
    assert.equal(result.type, TEXT_FILTER, 'type should match');
    assert.equal(result.searchTerm, '(text)', 'complexFilterText should match');
  });

  test('transformTextToPillData returns multiple pills when flag is true', function(assert) {
    const text = 'medium = 3 && b = \'google.com\'';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 2);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'value should match');
    assert.equal(result[1].type, QUERY_FILTER, 'type should match');
    assert.equal(result[1].meta, 'b', 'meta should match');
    assert.equal(result[1].operator, '=', 'operator should match');
    assert.equal(result[1].value, '\'google.com\'', 'value should match');
  });

  test('transformTextToPillData returns query pills and text pills', function(assert) {
    const text = `medium = 3 && ${SEARCH_TERM_MARKER}text filter${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 2);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'medium', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '3', 'value should match');
    assert.equal(result[1].type, TEXT_FILTER, 'type should match');
    assert.equal(result[1].searchTerm, 'text filter', 'complexFilterText should match');
  });

  test('transformTextToPillData will not return more than one text filter', function(assert) {
    const text = `${SEARCH_TERM_MARKER}text${SEARCH_TERM_MARKER} && ${SEARCH_TERM_MARKER}text 2${SEARCH_TERM_MARKER}`;
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, TEXT_FILTER, 'type should match');
    assert.equal(result[0].searchTerm, 'text', 'complexFilterText should match');
  });

  test('transformTextToPillData returns as little as a complex pill as possible when using OR (||)', function(assert) {
    const text = 'b = \'google.com\' && medium = 2 || medium = 3';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 2);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'b', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'google.com\'', 'value should match');
    assert.equal(result[1].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[1].complexFilterText, '(medium = 2 || medium = 3)', 'complexFilterText should match');
  });

  test('transformTextToPillData returns as little as a complex pill as possible when using OR (||) with normal pills after', function(assert) {
    const text = 'b = \'google.com\' && medium = 2 || medium = 3 && referer exists';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 3);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'b', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'google.com\'', 'value should match');
    assert.equal(result[1].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[1].complexFilterText, '(medium = 2 || medium = 3)', 'complexFilterText should match');
    assert.equal(result[2].type, QUERY_FILTER, 'type should match');
    assert.equal(result[2].meta, 'referer', 'meta should match');
    assert.equal(result[2].operator, 'exists', 'operator should match');
    assert.notOk(result[2].value, 'value should not exist');
  });

  test('transformTextToPillData looks one deep into parenthesis, but not deeper', function(assert) {
    const text = '(b = \'google.com\') && ((b = \'google.com\'))';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 2);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'b', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '\'google.com\'', 'value should match');
    assert.equal(result[1].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[1].complexFilterText, '((b = \'google.com\'))', 'complexFilterText should match');
  });

  test('transformTextToPillData does not break order of operations when combining OR into one complex pill', function(assert) {
    const text = "(filename = 'firefox-33.1.1.complete.mar' || (sessionid = 80 && medium exists)) || b exists";
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, "((filename = 'firefox-33.1.1.complete.mar' || (sessionid = 80 && medium exists)) || b exists)", 'complexFilterText should match');
  });

  test('transformTextToPillData does not break order of operations when combining OR into one complex pill and tries to turn normal criteria into pills', function(assert) {
    const text = 'sessionid = 80 && b exists || (medium = 1)';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 2);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'sessionid', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '80', 'value should match');
    assert.equal(result[1].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[1].complexFilterText, '(b exists || (medium = 1))', 'complexFilterText should match');
  });

  test('transformTextToPillData returns complex pill for non-indexed meta key', function(assert) {
    const text = 'ip.addr exists';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, COMPLEX_FILTER, 'type should match');
    assert.equal(result[0].complexFilterText, 'ip.addr exists', 'complexFilterText should match');
  });

  test('transformTextToPillData returns an invalid pill for a mismatched type', function(assert) {
    const text = 'medium = \'foo\'';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
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
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'c', 'meta should match');
    assert.equal(result[0].operator, 'length', 'operator should match');
    assert.equal(result[0].value, '-3', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter a non-negative Integer.', 'validation error should be correct');
  });

  // Leaving here to remind me to implement this soon
  skip('transformTextToPillData returns an invalid pill for an out-of-range integer value', function(assert) {
    // bytes.src is UInt64, that number is 2^65
    const text = 'bytes.src = 36893488147419103000';

    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 1);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'bytes.src', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '36893488147419103000', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter a 64-bit Integer.', 'validation error should be correct');
  });

  test('transformTextToPillData returns invalid pills alongside valid pills', function(assert) {
    const text = 'alias.ip = 8080 && medium = 3';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES, false, true);
    assert.strictEqual(result.length, 2);
    assert.equal(result[0].type, QUERY_FILTER, 'type should match');
    assert.equal(result[0].meta, 'alias.ip', 'meta should match');
    assert.equal(result[0].operator, '=', 'operator should match');
    assert.equal(result[0].value, '8080', 'value should match');
    assert.ok(result[0].isInvalid, 'pill should be invalid');
    assert.equal(result[0].validationError.string, 'You must enter an IPv4 address.', 'validation error should be correct');
    assert.equal(result[1].meta, 'medium', 'forward slash was not parsed correctly');
    assert.equal(result[1].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[1].value, '3', 'forward slash was not parsed correctly');
  });

  test('parsePillDataFromUri correctly parses forward slashes and operators into pills', function(assert) {
    const result = parsePillDataFromUri(params.mf, DEFAULT_LANGUAGES);
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '\'reston=\\\'virginia.sys\'', 'forward slash was not parsed correctly');
  });

  test('parsePillDataFromUri correctly parses multiple params', function(assert) {
    const result = parsePillDataFromUri('filename%20%3D%20\'reston%3D%5C\'virginia.sys\'/medium%20%3D%202', DEFAULT_LANGUAGES);
    assert.equal(result.length, 2, 'two pills came out');
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '\'reston=\\\'virginia.sys\'', 'forward slash was not parsed correctly');
    assert.equal(result[1].meta, 'medium', 'forward slash was not parsed correctly');
    assert.equal(result[1].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[1].value, '2', 'forward slash was not parsed correctly');
  });

  test('uriEncodeMetaFilters can convert a pill array to a string suitable for the metaFilter query param', function(assert) {
    const queryPill = transformTextToPillData('medium = 1', DEFAULT_LANGUAGES);
    const encQP = encodeURIComponent('medium = 1');
    const complexPill = transformTextToPillData('(bar)', DEFAULT_LANGUAGES);
    const encCP = encodeURIComponent('(bar)');
    const textPill = transformTextToPillData(`${SEARCH_TERM_MARKER}baz${SEARCH_TERM_MARKER}`, DEFAULT_LANGUAGES);
    const unencTP = `${SEARCH_TERM_MARKER}baz${SEARCH_TERM_MARKER}`; // Text Filters are not encoded
    const empty = transformTextToPillData('', DEFAULT_LANGUAGES);
    assert.equal(uriEncodeMetaFilters([queryPill]), encQP, 'query pill only');
    assert.equal(uriEncodeMetaFilters([complexPill]), encCP, 'complex pill only');
    assert.equal(uriEncodeMetaFilters([textPill]), unencTP, 'text pill only');
    assert.equal(uriEncodeMetaFilters([queryPill, complexPill]), `${encQP}/${encCP}`, 'query and complex pills');
    assert.equal(uriEncodeMetaFilters([queryPill, textPill]), `${encQP}/${unencTP}`, 'query and text pills');
    assert.equal(uriEncodeMetaFilters([complexPill, textPill]), `${encCP}/${unencTP}`, 'complex and text pills');
    assert.equal(uriEncodeMetaFilters([queryPill, complexPill, textPill]), `${encQP}/${encCP}/${unencTP}`, 'query, complex, and text pills');
    assert.deepEqual(uriEncodeMetaFilters([empty]), undefined, 'empty pill');
    assert.equal(uriEncodeMetaFilters([queryPill, empty]), `${encQP}`, 'query and empty pills');
    assert.equal(uriEncodeMetaFilters([queryPill, empty, textPill]), `${encQP}/${unencTP}`, 'query, empty, and text pills');
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
    // IMPLIED OR
    assert.ok(hasComplexText(','), 'Missed detecting ","');
    assert.ok(hasComplexText('x,x'), 'Missed detecting ","');
    // RANGE
    assert.ok(hasComplexText('-'), 'Missed detecting "-"');
    assert.ok(hasComplexText('x-x'), 'Missed detecting "-"');
    // LENGTH
    assert.ok(hasComplexText('length'), 'Missed detecting "length"');
    assert.ok(hasComplexText('xlengthx'), 'Missed detecting "length"');
    // REGEX
    assert.ok(hasComplexText('regex'), 'Missed detecting "regex"');
    assert.ok(hasComplexText('xregexx'), 'Missed detecting "regex"');
    // <, <=, >, >=
    assert.ok(hasComplexText('<'), 'Missed detecting "<"');
    assert.ok(hasComplexText('<='), 'Missed detecting "<="');
    assert.ok(hasComplexText('>'), 'Missed detecting ">"');
    assert.ok(hasComplexText('>='), 'Missed detecting ">="');
    assert.notOk(hasComplexText('='), 'Improperly detected "=" as complex');
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
});
