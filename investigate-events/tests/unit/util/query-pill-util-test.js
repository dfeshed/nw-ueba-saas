import { module, test } from 'qunit';
import { determineNewComponentPropsFromPillData } from 'investigate-events/components/query-container/query-pill/query-pill-util';

module('Unit | Util | query-pill-util');

const metaConfig = { format: 'Text', metaName: 'alert', flags: -2147483133, displayName: 'Alerts', indexedBy: 'value' };
const eqOperator = { displayName: '=', description: 'Equals', isExpensive: false, hasValue: true };
const existsOperator = { displayName: 'exists', description: 'Exists', isExpensive: false, hasValue: false };

test('properties when meta string is passed in', function(assert) {
  assert.expect(1);
  const expectedPropeties = {
    prepopulatedMetaText: 'foo',
    isMetaAutoFocused: true,
    isMetaActive: true
  };
  const pillData = {
    meta: 'foo'
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta should be active');
});

test('properties when meta object is passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfig,
    isMetaActive: false,
    isMetaAutoFocused: true,
    isOperatorActive: true
  };
  const pillData = {
    meta: metaConfig
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta is set, operator is active');
});

test('properties when meta object and operator string is passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfig,
    isMetaActive: false,
    isMetaAutoFocused: true,
    prepopulatedOperatorText: 'foo',
    isOperatorActive: true
  };
  const pillData = {
    meta: metaConfig,
    operator: 'foo'
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta is set, operator is active with prepopulated string');
});

test('properties when meta object and operator object are passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfig,
    isMetaActive: false,
    isMetaAutoFocused: true,
    selectedOperator: eqOperator,
    isOperatorActive: false,
    isValueActive: true
  };
  const pillData = {
    meta: metaConfig,
    operator: eqOperator
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta and operator are set, value is active as operator accepts values');
});

test('properties when meta object and operator object which does not accept values are passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfig,
    isMetaActive: false,
    isMetaAutoFocused: true,
    selectedOperator: existsOperator,
    isOperatorActive: true
  };
  const pillData = {
    meta: metaConfig,
    operator: existsOperator
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta and operator are set with operator active as it does not accpet any values');
});

test('properties when meta and operator object with value are passed in', function(assert) {
  const expectedPropeties = {
    selectedMeta: metaConfig,
    isMetaActive: false,
    isMetaAutoFocused: true,
    selectedOperator: eqOperator,
    isOperatorActive: false,
    valueString: 'boo',
    isValueActive: true
  };
  const pillData = {
    meta: metaConfig,
    operator: eqOperator,
    value: 'boo'
  };
  const properties = determineNewComponentPropsFromPillData(pillData);
  assert.deepEqual(properties, expectedPropeties, 'Meta and operator are set, value is active with a string');
});