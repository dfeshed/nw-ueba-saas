import { module, test } from 'qunit';

import { serializeQueryParams } from 'investigate-shared/utils/query-utils';
import { encodeMetaFilterConditions, _isValidQueryFilter } from 'investigate-shared/actions/api/events/utils';

module('Unit | Helper | utils');

const conditions = [{
  meta: 'foo',
  operator: '=',
  value: 'bar'
}, {
  meta: 'foo',
  operator: 'exists',
  value: ''
}, {
  meta: 'foo',
  operator: 'begins',
  value: '\'//\''
}];

const filters = [{
  meta: 'foo',
  operator: undefined,
  value: undefined
}];

const complexConditions = [{
  complexFilterText: 'foo=\'bar\'||foo=baz'
}, {
  complexFilterText: 'bar=\'foo\'||baz=foo'
}];

const params = {
  et: 0,
  eid: 1,
  mf: 'a%3D\'a/%3Db%3D/a\'',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3
};

test('serializeQueryParams gives the correct URI string', function(assert) {
  assert.expect(1);
  const result = serializeQueryParams(params);
  assert.equal(result, 'et=0&eid=1&mf=a%3D\'a/%3Db%3D/a\'&mps=default&rs=max&sid=2&st=3', 'serializeQueryParams gives the correct URL string');
});

test('encodeMetaFilterConditions correctly encodes conditions', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(conditions);

  assert.equal(result, 'foo = bar && foo exists  && foo begins \'//\'');
});

test('encodeMetaFilterConditions correctly encodes complex filters', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(complexConditions);

  assert.equal(result, 'foo=\'bar\'||foo=baz && bar=\'foo\'||baz=foo');
});

test('encodeMetaFilterConditions returns values which exist', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(filters);

  assert.equal(result, 'foo  ');
});

test('encodeMetaFilterConditions returns empty string when properties are undefined', function(assert) {
  assert.expect(1);
  const filters = [{}];
  const result = encodeMetaFilterConditions(filters);

  assert.equal(result, '');
});

test('encodeMetaFilterConditions returns relevant string based on valid objects', function(assert) {
  assert.expect(1);
  const filters = [
    {
      meta: 'foo',
      operator: '=',
      value: 'bar'
    },
    {},
    {
      complexFilterText: 'medium = 1 || medium = 32'
    },
    {
      meta: 'foo',
      value: 'exists'
    },
    {}
  ];
  const result = encodeMetaFilterConditions(filters);

  assert.equal(result, 'foo = bar && medium = 1 || medium = 32 && foo  exists');
});

test('_isValidQueryFilter returns true for a valid filter attribute', function(assert) {
  assert.expect(1);
  const filters = { meta: 'foo' };
  const result = _isValidQueryFilter(filters);

  assert.ok(result, 'Expected Filter');
});

test('_isValidQueryFilter returns true for a valid complex filter', function(assert) {
  assert.expect(1);
  const filters = { complexFilterText: 'medium = 1 || medium =32' };
  const result = _isValidQueryFilter(filters);

  assert.ok(result, 'Expected Filter');
});


test('_isValidQueryFilter returns false for a invalid filter', function(assert) {
  assert.expect(1);
  const filters = {};
  const result = _isValidQueryFilter(filters);

  assert.notOk(result, 'Discarded Filter');
});