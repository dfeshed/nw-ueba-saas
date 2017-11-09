import queryUtils from 'investigate-events/actions/helpers/query-utils';
import { module, test } from 'qunit';

module('Unit | Helper | query utils');

const baseUri = '123/0/1484157289';

const simpleUriMeta = 'service';
const simpleUriOperator = '=';
const simpleUriValue = '21';
const simpleUri = `${baseUri}/${simpleUriMeta}%20${simpleUriOperator}%20${simpleUriValue}`;

const simpleUriMeta2 = 'tld';
const simpleUriOperator2 = '>';
const simpleUriValue2 = 'com';
const simpleUri2 = `${simpleUriMeta2}%20${simpleUriOperator2}%20${simpleUriValue2}`;

const emptyMetaUri = `${simpleUri}//`;

const multiPairUri = `${simpleUri}/${simpleUri2}`;

test('parseEventQueryUri correctly parses URI', function(assert) {
  let result;

  assert.expect(12);

  result = queryUtils.parseEventQueryUri(simpleUri).metaFilter.conditions;
  assert.equal(result.length, 1, 'Only one key/value pair extracted for simple URIs');
  assert.equal(result[0].meta, simpleUriMeta, 'Meta key extracted for simple URIs');
  assert.equal(result[0].operator, simpleUriOperator, 'Operator extracted for simple URIs');
  assert.equal(result[0].value, simpleUriValue, 'Value extracted for simple URIs');

  result = queryUtils.parseEventQueryUri(emptyMetaUri).metaFilter.conditions;
  assert.equal(result.length, 1, 'Only one key/value pair extracted when there are extra /\'s');

  result = queryUtils.parseEventQueryUri(multiPairUri).metaFilter.conditions;
  assert.equal(result.length, 2, 'Two key/value pairs extracted when there are multiple pairs in the URL');
  assert.equal(result[0].meta, simpleUriMeta, 'First meta key extracted');
  assert.equal(result[0].operator, simpleUriOperator, 'First operator extracted');
  assert.equal(result[0].value, simpleUriValue, 'First value extracted');
  assert.equal(result[1].meta, simpleUriMeta2, 'Second meta key extracted');
  assert.equal(result[1].operator, simpleUriOperator2, 'Second operator extracted');
  assert.equal(result[1].value, simpleUriValue2, 'Second value extracted');
});
