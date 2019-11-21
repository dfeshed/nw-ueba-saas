import { module, test } from 'qunit';
import { OPERATOR_AND, OPERATOR_OR, QUERY_FILTER, TEXT_FILTER } from 'investigate-events/constants/pill';
import { markTextPillAttachedOperators } from 'investigate-events/util/logical-operator-helper';

const data1 = [
  { type: TEXT_FILTER, searchTerm: 'foobar' },
  { type: OPERATOR_AND },
  { type: QUERY_FILTER, meta: 'medium', operator: '=', value: '1' },
  { type: OPERATOR_OR },
  { type: QUERY_FILTER, meta: 'medium', operator: '=', value: '2' }
];

const data2 = [
  { type: QUERY_FILTER, meta: 'medium', operator: '=', value: '1' },
  { type: OPERATOR_AND },
  { type: TEXT_FILTER, searchTerm: 'foobar' },
  { type: OPERATOR_OR },
  { type: QUERY_FILTER, meta: 'medium', operator: '=', value: '2' }
];

const data3 = [
  { type: QUERY_FILTER, meta: 'medium', operator: '=', value: '1' },
  { type: OPERATOR_AND },
  { type: QUERY_FILTER, meta: 'medium', operator: '=', value: '2' },
  { type: OPERATOR_AND },
  { type: TEXT_FILTER, searchTerm: 'foobar' }
];

module('Unit | Util | keys', function() {
  test('Marks an operator as attached before a text pill as the first pill', function(assert) {
    const result = markTextPillAttachedOperators(data1);
    assert.strictEqual(result[1].type, OPERATOR_AND, 'first operator is AND');
    assert.ok(result[1].isTextPillAttached, 'first operator is attached to text filter');
    assert.strictEqual(result[3].type, OPERATOR_OR, 'second operator is OR');
    assert.notOk(result[3].isTextPillAttached, 'second operator is NOT attached to text filter');
  });

  test('Marks an operator as attached before a text pill as the third pill', function(assert) {
    const result = markTextPillAttachedOperators(data2);
    assert.strictEqual(result[1].type, OPERATOR_AND, 'first operator is AND');
    assert.ok(result[1].isTextPillAttached, 'first operator is attached to text filter');
    assert.strictEqual(result[3].type, OPERATOR_OR, 'second operator is OR');
    assert.notOk(result[3].isTextPillAttached, 'second operator is NOT attached to text filter');
  });

  test('Marks an operator as attached before a text pill as the fifth pill', function(assert) {
    const result = markTextPillAttachedOperators(data3);
    assert.strictEqual(result[1].type, OPERATOR_AND, 'first operator is AND');
    assert.notOk(result[1].isTextPillAttached, 'first operator is NOT attached to text filter');
    assert.strictEqual(result[3].type, OPERATOR_AND, 'second operator is AND');
    assert.ok(result[3].isTextPillAttached, 'second operator is attached to text filter');
  });
});