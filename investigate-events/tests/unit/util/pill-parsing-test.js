import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import {
  isSearchTerm,
  parsePillDataFromUri,
  transformTextToPillData,
  uriEncodeMetaFilters
} from 'investigate-events/util/query-parsing';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';

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

module('Unit | Util | Pill Parsing', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('transformTextToPillData returns complex filter object because of ||', function(assert) {
    const freeFormText = 'medium = 1 || medium = 32';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      // note is wrapped in quotes
      complexFilterText: '(medium = 1 || medium = 32)',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData treats lack of operator as a complex query', function(assert) {
    const freeFormText = 'medium';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: 'medium',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData treats bad meta as complex query', function(assert) {
    const freeFormText = 'lakjsdlakjsd = yeah';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: 'lakjsdlakjsd = yeah',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData treats operator that does not belong to meta as complex query', function(assert) {
    const freeFormText = 'sessionid contains 123';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: 'sessionid contains 123',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData treats operator that requires value but does not have one as complex query', function(assert) {
    const freeFormText = 'medium =';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: 'medium =',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData treats operator that require no value but has one as complex query', function(assert) {
    const freeFormText = 'medium exists 10';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: 'medium exists 10',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData handles when just meta and operator', function(assert) {
    const freeFormText = 'medium exists';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: undefined,
      meta: 'medium',
      operator: 'exists',
      value: undefined
    });
  });

  test('transformTextToPillData returns pill data object', function(assert) {
    const freeFormText = 'medium = 1';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(
      result,
      { meta: 'medium', operator: '=', value: '1', complexFilterText: undefined }
    );
  });

  test('transformTextToPillData returns populated pill object even if operator embedded in value', function(assert) {
    const freeFormText = 'user.dst = \'1=2\'';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: undefined,
      meta: 'user.dst',
      operator: '=',
      value: '\'1=2\''
    });
  });

  test('transformTextToPillData handles surrounding white space', function(assert) {
    const freeFormText = 'medium exists ';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: undefined,
      meta: 'medium',
      operator: 'exists',
      value: undefined
    });
  });

  test('transformTextToPillData treats operator that has extra text as complex query', function(assert) {
    const freeFormText = 'medium =foo';
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      complexFilterText: 'medium =foo',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData returns complex pill when forced to do so', function(assert) {
    const freeFormText = 'medium = foo';
    const shouldForceComplex = true;
    const result = transformTextToPillData(freeFormText, DEFAULT_LANGUAGES, shouldForceComplex);
    assert.deepEqual(result, {
      complexFilterText: '(medium = foo)',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData returns text filter object because of Text filter marker', function(assert) {
    const text = '~some random text';
    const result = transformTextToPillData(text, DEFAULT_LANGUAGES);
    assert.deepEqual(result, {
      searchTerm: '~some random text'
    });
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
    const queryPill = { meta: 'action', operator: '=', value: 'foo' };
    const encQP = encodeURIComponent('action = foo');
    const complexPill = { complexFilterText: '(bar)' };
    const encCP = encodeURIComponent('(bar)');
    const textPill = { searchTerm: 'baz' };
    const encTP = encodeURIComponent('~baz');
    const empty = { meta: undefined, operator: undefined, value: undefined };
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
});