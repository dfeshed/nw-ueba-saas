import { module, test } from 'qunit';

import {
  createFilename,
  encodeMetaFilterConditions,
  extractSearchTermFromFilters,
  mergeFilterStrings,
  removeEmptyParens
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

test('mergeFilterStrings returns proper string', function(assert) {
  assert.equal(
    ['a'].reduce(mergeFilterStrings, ''),
    'a', 'single filter'
  );
  assert.equal(
    ['(', 'a', ')'].reduce(mergeFilterStrings, ''),
    '(a)', 'single filter within parens'
  );
  assert.equal(
    ['a', 'b'].reduce(mergeFilterStrings, ''),
    'a && b', 'two filters'
  );
  assert.equal(
    ['(', 'a', 'b', ')'].reduce(mergeFilterStrings, ''),
    '(a && b)', 'two filters within parens'
  );
  assert.equal(
    ['(', 'a', ')', '(', 'b', ')'].reduce(mergeFilterStrings, ''),
    '(a) && (b)', 'two groups'
  );
  assert.equal(
    ['(', '(', 'a', ')', '(', 'b', ')', ')'].reduce(mergeFilterStrings, ''),
    '((a) && (b))', 'two groups within parens'
  );
  assert.equal(
    ['(', 'a', '(', 'b', ')', ')'].reduce(mergeFilterStrings, ''),
    '(a && (b))', 'nested groups'
  );
  assert.equal(
    ['(', '(', 'a', '(', 'b', ')', ')', '(', '(', 'c', ')', 'd', ')', ')'].reduce(mergeFilterStrings, ''),
    '((a && (b)) && ((c) && d))', 'deeply nested groups'
  );
});

test('removeEmptyParens properly removes empty parens', function(assert) {
  const op = { type: 'open-paren' };
  const cp = { type: 'close-paren' };
  const query = { type: 'query' };
  const complex = { type: 'complex' };
  const text = { type: 'text' };

  // Q C T
  const allFilterTypes = [query, complex, text];
  // ( Q C T )
  const wrappedFilters = [op, ...allFilterTypes, cp];
  // ( ( ( Q C T ) ) )
  const deeplyWrappedFilters = [op, op, op, ...allFilterTypes, cp, cp, cp];
  // ( )
  const emptyParens = [op, cp];
  // ( ( ) )
  const nestedEmptyParens = [op, ...emptyParens, ...emptyParens, cp];
  // ( Q ( ) ( ) Q )
  const nestedEmptyParensWithFilters = [op, query, ...nestedEmptyParens, query, cp];
  // Q ( ( ( ) ) )
  const deeplyNestedParensWithFilter = [query, op, op, op, cp, cp, cp];
  // ( Q Q ( C ( ) ( Q ( ( ) Q ) ( ) ) ( ) ) ) T
  const wtfParens = [op, query, query, op, complex, ...emptyParens, op, query, op, ...emptyParens, query, cp, ...emptyParens, cp, ...emptyParens, cp, cp, text];

  assert.deepEqual(removeEmptyParens(allFilterTypes), allFilterTypes, 'just filters');
  assert.deepEqual(removeEmptyParens(wrappedFilters), wrappedFilters, 'wrapped filters');
  assert.deepEqual(removeEmptyParens(deeplyWrappedFilters), deeplyWrappedFilters, 'deeply wrapped filters');
  assert.deepEqual(removeEmptyParens(emptyParens), [], 'empty parens');
  assert.deepEqual(removeEmptyParens(nestedEmptyParens), [], 'nested empty parens');
  assert.deepEqual(removeEmptyParens(nestedEmptyParensWithFilters), [op, query, query, cp], 'nested empty parens with filters');
  assert.deepEqual(removeEmptyParens(deeplyNestedParensWithFilter), [query], 'deeply nested parens with filter');
  assert.deepEqual(removeEmptyParens(wtfParens), [
    op, query, query, op, complex, op, query, op, query, cp, cp, cp, cp, text
  ], 'insanely nested parens with filters');
});