import { module, test } from 'qunit';
import { determineNewComponentPropsFromPillData, resultsCount, matcher } from 'investigate-events/components/query-container/query-pill/query-pill-util';
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

test('matcher function should find an index if text is present', function(assert) {

  const m1 = { displayName: 'foo', metaName: 'bar' };
  const m2 = { displayName: 'bar', metaName: 'baz' };
  const m3 = { displayName: 'bar', metaName: 'baz' };

  assert.equal(matcher(m1, 'foo'), 0, 'Did not find item in "displayName"');
  assert.equal(matcher(m1, 'baz'), -1, 'Found item but should not have');
  assert.equal(matcher(m2, 'foo'), -1, 'Found item but should not have');
  assert.equal(matcher(m2, 'baz'), 0, 'Did not find item in "metaName"');
  assert.equal(matcher(m3, '   baz'), 0, 'Did not ignore leading spaces');
});
