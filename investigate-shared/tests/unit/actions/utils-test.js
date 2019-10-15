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
  type: 'operator-and'
}, {
  meta: 'foo',
  operator: 'exists',
  value: ''
}, {
  type: 'operator-or'
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
  { type: 'operator-and' },
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
  type: 'operator-and'
}, {
  complexFilterText: 'bar=\'foo\'||baz=foo'
}];

const textPillInMiddle = [
  {
    'id': 'guidedPill_14',
    'meta': 'action',
    'operator': '=',
    'value': 'foo',
    'type': 'query'
  },
  {
    'type': 'operator-and',
    'id': 'guidedPill_15'
  },
  {
    'id': 'guidedPill_16',
    'searchTerm': 'bar',
    'type': 'text'
  },
  {
    'type': 'operator-and',
    'id': 'guidedPill_17'
  },
  {
    'id': 'guidedPill_19',
    'meta': 'action',
    'operator': '=',
    'value': 'baz',
    'type': 'query'
  }
];

const textPillAtBeginning = [
  {
    'id': 'guidedPill_16',
    'searchTerm': 'bar',
    'type': 'text'
  },
  {
    'type': 'operator-and',
    'id': 'guidedPill_17'
  },
  {
    'id': 'guidedPill_19',
    'meta': 'action',
    'operator': '=',
    'value': 'baz',
    'type': 'query'
  }
];

const textPillAtEnd = [
  {
    'id': 'guidedPill_19',
    'meta': 'action',
    'operator': '=',
    'value': 'baz',
    'type': 'query'
  },
  {
    'type': 'operator-and',
    'id': 'guidedPill_17'
  },
  {
    'id': 'guidedPill_16',
    'searchTerm': 'bar',
    'type': 'text'
  }
];

test('encodeMetaFilterConditions correctly encodes conditions', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(conditions);

  assert.equal(result, 'foo = bar AND foo exists OR foo begins \'//\'');
});

test('encodeMetaFilterConditions correctly encodes complex filters', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(complexConditions);

  assert.equal(result, 'foo=\'bar\'||foo=baz AND bar=\'foo\'||baz=foo');
});

test('encodeMetaFilterConditions correctly encodes back-to-back parens', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(parenConditions);

  assert.equal(result, '(foo = bar) AND (foo != bar)');
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
    }, {
      type: 'operator-and'
    }, {
      // empty
    }, {
      complexFilterText: 'medium = 1 || medium = 32'
    }, {
      type: 'operator-and'
    }, {
      meta: 'foo',
      value: 'exists'
    },
    {}
  ];
  const result = encodeMetaFilterConditions(filters);

  assert.equal(result, 'foo = bar AND medium = 1 || medium = 32 AND foo  exists');
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
    { operator: 'will be removed' },
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

test('extractSearchTermFromFilters handles text filters correctly', function(assert) {
  assert.expect(11);

  let results = extractSearchTermFromFilters(textPillInMiddle);

  assert.equal(results.searchTerm, 'bar', 'Search term should be extracted from array of pills');
  assert.equal(results.metaFilters.length, 3, 'should be 3 pills left');
  assert.equal(results.metaFilters[0].type, 'query', 'first should be query pill');
  assert.equal(results.metaFilters[1].type, 'operator-and', 'second should be operator');
  assert.equal(results.metaFilters[2].type, 'query', 'third should be query pill');

  results = extractSearchTermFromFilters(textPillAtBeginning);

  assert.equal(results.searchTerm, 'bar', 'Search term should be extracted from array of pills');
  assert.equal(results.metaFilters.length, 1, 'should be 1 pill left');
  assert.equal(results.metaFilters[0].type, 'query', 'only should be query pill');

  results = extractSearchTermFromFilters(textPillAtEnd);

  assert.equal(results.searchTerm, 'bar', 'Search term should be extracted from array of pills');
  assert.equal(results.metaFilters.length, 1, 'should be 1 pill left');
  assert.equal(results.metaFilters[0].type, 'query', 'only should be query pill');
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
    ['a', 'AND', 'b'].reduce(mergeFilterStrings, ''),
    'a AND b', 'two filters'
  );
  assert.equal(
    ['(', 'a', 'AND', 'b', ')'].reduce(mergeFilterStrings, ''),
    '(a AND b)', 'two filters within parens'
  );
  assert.equal(
    ['(', 'a', ')', 'OR', '(', 'b', ')'].reduce(mergeFilterStrings, ''),
    '(a) OR (b)', 'two groups'
  );
  assert.equal(
    ['(', '(', 'a', ')', 'OR', '(', 'b', ')', ')'].reduce(mergeFilterStrings, ''),
    '((a) OR (b))', 'two groups within parens'
  );
  assert.equal(
    ['(', 'a', 'OR', '(', 'b', ')', ')'].reduce(mergeFilterStrings, ''),
    '(a OR (b))', 'nested groups'
  );
  assert.equal(
    ['(', '(', 'a', 'AND', '(', 'b', ')', ')', 'OR', '(', '(', 'c', ')', 'AND', 'd', ')', ')'].reduce(mergeFilterStrings, ''),
    '((a AND (b)) OR ((c) AND d))', 'deeply nested groups'
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