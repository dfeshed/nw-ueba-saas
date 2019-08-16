import { module, test } from 'qunit';

import {
  createFilename,
  encodeMetaFilterConditions,
  extractSearchTermFromFilters
} from 'investigate-shared/actions/api/events/utils';

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

const parenConditions = [
  { type: 'open-paren' },
  {
    meta: 'foo',
    operator: '=',
    value: 'bar'
  },
  { type: 'close-paren' },
  { type: 'open-paren' },
  {
    meta: 'foo',
    operator: '!=',
    value: 'bar'
  },
  { type: 'close-paren' }
];

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

test('encodeMetaFilterConditions correctly encodes conditions', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(conditions);

  assert.equal(result, 'foo = bar && foo exists && foo begins \'//\'');
});

test('encodeMetaFilterConditions correctly encodes complex filters', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(complexConditions);

  assert.equal(result, 'foo=\'bar\'||foo=baz && bar=\'foo\'||baz=foo');
});

test('encodeMetaFilterConditions correctly encodes back-to-back parens', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(parenConditions);

  assert.equal(result, '(foo = bar) && (foo != bar)');
});

test('encodeMetaFilterConditions returns values which exist', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(filters);

  assert.equal(result, 'foo');
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

test('createFilename returns proper fileName when selectAll is true', function(assert) {
  const eventType = 'NETWORK';
  const serviceName = 'Conc';
  const sessionIds = [];
  const isSelectAll = true;

  const result = createFilename(eventType, serviceName, sessionIds, isSelectAll);
  assert.equal(result, 'Conc_ALL_EVENTS_NETWORK', 'return correct file name');
});

test('createFilename returns proper fileName when there are multiple sessionIds', function(assert) {
  const eventType = 'NETWORK';
  const serviceName = 'Conc';
  const sessionIds = [1, 2, 3];
  const isSelectAll = false;

  const result = createFilename(eventType, serviceName, sessionIds, isSelectAll);
  assert.equal(result, 'Conc_3_EVENTS_NETWORK', 'return correct file name');
});

test('createFilename returns proper fileName when there is one sessionid', function(assert) {
  const eventType = 'NETWORK';
  const serviceName = 'Conc';
  const sessionIds = [3412];
  const isSelectAll = false;

  const result = createFilename(eventType, serviceName, sessionIds, isSelectAll);
  assert.equal(result, 'Conc_SID3412_NETWORK', 'return correct file name');
});

test('extractSearchTermFromFilters returns an array of pills without any text filters', function(assert) {
  assert.expect(3);
  const pills = [
    { meta: 'foo' },
    { searchTerm: 'bar' },
    { meta: 'baz' },
    { meta: 'bang' },
    { meta: 'boom' }
  ];
  const { metaFilters, searchTerm } = extractSearchTermFromFilters(pills);
  assert.equal(metaFilters.length, 4, 'Text pill was not removed from array of pills');
  assert.deepEqual(metaFilters, [
    { meta: 'foo' },
    { meta: 'baz' },
    { meta: 'bang' },
    { meta: 'boom' }
  ]);
  assert.equal(searchTerm, 'bar', 'Search term should be extracted from array of pills');
});
