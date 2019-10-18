import { module, test } from 'qunit';
import { determineNewComponentPropsFromPillData, resultsCount, searcher } from 'investigate-events/components/query-container/query-pill/query-pill-util';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';
import { metaIsIndexedByNoneUInt16 as metaConfigIsIndexedByNone,
  metaIsIndexedByValueText as metaConfigIsIndexedByValue
} from '../../helpers/meta-data-helper';

module('Unit | Util | query-pill-util');

const eqOperator = { displayName: '=', description: 'Equals', isExpensive: false, hasValue: true };
const existsOperator = { displayName: 'exists', description: 'Exists', isExpensive: false, hasValue: false };

test('properties when meta string is passed in', function(assert) {
  assert.expect(1);
  const expectedPropeties = {
    prepopulatedMetaText: 'foo',
    isMetaAutoFocused: true,
    isMetaActive: true,
    selectedMeta: null,
    selectedOperator: null,
    isOperatorActive: false,
    valueString: null,
    isValueActive: false
  };
  const pillData = {
    meta: 'foo'
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Did not find the correct properties');
});

test('properties when meta object is passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfigIsIndexedByValue,
    isMetaActive: false,
    isMetaAutoFocused: true,
    isOperatorActive: true
  };
  const pillData = {
    meta: metaConfigIsIndexedByValue
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta is set, operator is active');
});

test('properties when meta object and operator string is passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfigIsIndexedByValue,
    isMetaActive: false,
    isMetaAutoFocused: true,
    prepopulatedOperatorText: 'foo',
    isOperatorActive: true,
    selectedOperator: null,
    valueString: null,
    isValueActive: false
  };
  const pillData = {
    meta: metaConfigIsIndexedByValue,
    operator: 'foo'
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta is set, operator is active with prepopulated string');
});

test('properties when meta object and operator object are passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfigIsIndexedByValue,
    isMetaActive: false,
    isMetaAutoFocused: true,
    selectedOperator: eqOperator,
    isOperatorActive: false,
    isValueActive: true,
    valueString: ''
  };
  const pillData = {
    meta: metaConfigIsIndexedByValue,
    operator: eqOperator
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta and operator are set, value is active as operator accepts values');
});

test('properties when meta object and operator object which does not accept values are passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfigIsIndexedByValue,
    isMetaActive: false,
    isMetaAutoFocused: true,
    selectedOperator: existsOperator,
    isOperatorActive: true
  };
  const pillData = {
    meta: metaConfigIsIndexedByValue,
    operator: existsOperator
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta and operator are set with operator active as it does not accpet any values');
});

test('properties when meta and operator object with value are passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfigIsIndexedByValue,
    isMetaActive: false,
    isMetaAutoFocused: true,
    selectedOperator: eqOperator,
    isOperatorActive: false,
    valueString: 'boo',
    isValueActive: true
  };
  const pillData = {
    meta: metaConfigIsIndexedByValue,
    operator: eqOperator,
    value: 'boo'
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta and operator are set, value is active with a string');
});

test('provides a correct count for text passed in', function(assert) {

  const count = resultsCount(DEFAULT_LANGUAGES, 'al');
  assert.equal(count, 4, 'Matcher function not returning a correct count');
});

test('provides a correct valid meta count for text passed in', function(assert) {
  const DEFAULT_LANGUAGES2 = [
    ...DEFAULT_LANGUAGES,
    {
      ...metaConfigIsIndexedByNone,
      metaName: 'alert.xyz',
      displayName: 'Alert XYZ',
      formattedName: 'Alert XYZ'
    }
  ];
  const count = resultsCount(DEFAULT_LANGUAGES2, 'al');
  assert.equal(count, 4, 'Matcher function not returning a correct count');
});

test('provides a count 0 when no text is passed in', function(assert) {

  const count = resultsCount(DEFAULT_LANGUAGES, ' ');
  assert.equal(count, 0, 'Matcher function not returning a correct count');
});

test('provides a count 0 when there is no valid meta', function(assert) {

  const randomMetaName = `random-meta-${Date.now()}`;
  const metaConfigIsIndexedByNone1 = {
    ...metaConfigIsIndexedByNone,
    metaName: randomMetaName,
    displayName: 'TEST'
  };
  const languagesWithIndexedByNone = [...DEFAULT_LANGUAGES, metaConfigIsIndexedByNone1];
  const count = resultsCount(languagesWithIndexedByNone, randomMetaName);
  assert.equal(count, 0, 'Matcher function not returning a correct count');
});

test('searcher function finds appropriate meta given a search term', function(assert) {
  const outputMeta = searcher('foo', []);
  assert.deepEqual(outputMeta, [], 'searcher doesnt choke on empty array');

  const metas1 = [{
    metaName: 'bar',
    displayName: 'Bar'
  }];
  const outputMetas1 = searcher('foo', metas1);
  assert.deepEqual(outputMetas1, [], 'searcher handles finding nothing');

  const metas2 = [{
    metaName: 'Bar',
    displayName: 'nope'
  }, {
    metaName: 'Nopeynope',
    displayName: 'NopeToTheNope'
  }];
  const outputMetas2 = searcher('bar', metas2);
  assert.equal(outputMetas2.length, 1, 'searcher will find based on meta name with wrong case');
  assert.deepEqual(outputMetas2[0], metas2[0], 'searcher will find based on meta name with wrong case');

  const metas3 = [{
    metaName: 'foo',
    displayName: 'Bar'
  }, {
    metaName: 'Nopeynope',
    displayName: 'NopeToTheNope'
  }];
  const outputMetas3 = searcher('bar', metas3);
  assert.equal(outputMetas3.length, 1, 'searcher will find based on displayName with wrong case');
  assert.deepEqual(outputMetas3[0], metas3[0], 'searcher will find based on displayName with wrong case');

  const metas4 = [{
    metaName: 'foo Fooey foooooo',
    displayName: 'Bar'
  }, {
    metaName: 'Nopeynope',
    displayName: 'NopeToTheNope'
  }];
  const outputMetas4 = searcher('fooey', metas4);
  assert.equal(outputMetas4.length, 1, 'searcher will find based on partial meta name match with wrong case');
  assert.deepEqual(outputMetas4[0], metas4[0], 'searcher will find based on partial meta name with wrong case');

  const metas5 = [{
    metaName: 'foo Fooey foooooo',
    displayName: 'Bar Barrrrr Brr'
  }, {
    metaName: 'Nopeynope',
    displayName: 'NopeToTheNope'
  }];
  const outputMetas5 = searcher('barrrrr', metas5);
  assert.equal(outputMetas5.length, 1, 'searcher will find based on partial display name match with wrong case');
  assert.deepEqual(outputMetas5[0], metas5[0], 'searcher will find based on partial display name with wrong case');

});

test('searcher function orders results properly', function(assert) {

  const metas = [{
    metaName: 'foo',
    id: 1,
    displayName: 'Bar'
  }, {
    metaName: 'blahblah foo blahblah',
    id: 2,
    displayName: 'Bar'
  }, {
    metaName: 'bar',
    id: 3,
    displayName: 'asdkjasld foo'
  }, {
    metaName: 'blahblah foo',
    id: 4,
    displayName: 'Bar'
  }, {
    metaName: 'bar',
    id: 5,
    displayName: 'foo'
  }, {
    metaName: 'bar',
    id: 6,
    displayName: 'Bar'
  }, {
    metaName: 'bar',
    id: 7,
    displayName: 'aasdfooasdas'
  }, {
    metaName: 'foo',
    id: 8,
    displayName: 'Bar'
  }, {
    metaName: 'bar',
    id: 9,
    displayName: 'foo'
  }];

  const outputMetas = searcher('foo', metas).map((m) => m.id);
  assert.equal(outputMetas.length, 8, 'searcher will find 8 of 9, leaving out id 6');
  assert.deepEqual(outputMetas, [1, 8, 5, 9, 2, 4, 3, 7], 'searcher spits out metas in right order');

});
