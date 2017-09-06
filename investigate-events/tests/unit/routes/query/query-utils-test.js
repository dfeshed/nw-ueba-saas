import queryUtils from 'investigate-events/actions/helpers/query-utils';
import { module, test } from 'qunit';

module('Unit | Helper | query utils');

const baseUri = '123/0/1484157289';

const simpleUriKey = 'service';
const simpleUriValue = '21';
const simpleUri = `${baseUri}/${simpleUriKey}=${simpleUriValue}`;

const simpleUriKey2 = 'tld';
const simpleUriValue2 = 'com';
const simpleUri2 = `${simpleUriKey2}=${simpleUriValue2}`;

const complexUriKey = 'query';
const complexUriValue = 'partitionkey%3D\'reputation_ip\'%2Crowkey%3D\'1.1.1.1\'';
const complexUri = `${baseUri}/${complexUriKey}=${complexUriValue}`;

const emptyMetaUri = `${simpleUri}//`;

const multiPairUri = `${simpleUri}/${simpleUri2}`;

test('parseEventQueryUri correctly parses URI', function(assert) {
  let result;

  assert.expect(12);

  result = queryUtils.parseEventQueryUri(simpleUri).metaFilter.conditions;
  assert.equal(result.length, 1, 'Only one key/value pair extracted for simple URIs');
  assert.equal(result[0].key, simpleUriKey, 'Key extracted for simple URIs');
  assert.equal(result[0].value, simpleUriValue, 'Value extracted for simple URIs');

  result = queryUtils.parseEventQueryUri(complexUri).metaFilter.conditions;
  assert.equal(result.length, 1, 'Only one key/value pair extracted for complex URIs');
  assert.equal(result[0].key, complexUriKey, 'Key extracted for complex URIs');
  assert.equal(result[0].value, decodeURIComponent(complexUriValue), 'Value extracted for complex URIs');

  result = queryUtils.parseEventQueryUri(emptyMetaUri).metaFilter.conditions;
  assert.equal(result.length, 1, 'Only one key/value pair extracted when there are extra /\'s');

  result = queryUtils.parseEventQueryUri(multiPairUri).metaFilter.conditions;
  assert.equal(result.length, 2, 'Two key/value pairs extracted when there are multiple pairs in the URL');
  assert.equal(result[0].key, simpleUriKey, 'First key extracted');
  assert.equal(result[0].value, simpleUriValue, 'First value extracted');
  assert.equal(result[1].key, simpleUriKey2, 'Second key extracted');
  assert.equal(result[1].value, simpleUriValue2, 'Second value extracted');
});