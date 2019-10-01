import { module, test } from 'qunit';
import {
  CloseParen,
  OpenParen,
  OperatorAnd,
  OperatorOr
} from 'investigate-events/util/grammar-types';
import {
  CLOSE_PAREN,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR
} from 'investigate-events/constants/pill';

module('Unit | Util | Grammar Types');

test('can create a close paren', function(assert) {
  const closeParen = CloseParen.create();
  assert.equal(closeParen.componentName, 'query-container/close-paren');
  assert.equal(closeParen.id, undefined);
  assert.equal(closeParen.isFocused, false);
  assert.equal(closeParen.isSelected, false);
  assert.equal(closeParen.type, CLOSE_PAREN);
  assert.equal(closeParen.twinId, undefined);
});

test('can create an open paren', function(assert) {
  const openParen = OpenParen.create();
  assert.equal(openParen.componentName, 'query-container/open-paren');
  assert.equal(openParen.id, undefined);
  assert.equal(openParen.isFocused, false);
  assert.equal(openParen.isSelected, false);
  assert.equal(openParen.type, OPEN_PAREN);
  assert.equal(openParen.twinId, undefined);
});

test('can create a logical AND operator', function(assert) {
  const andOp = OperatorAnd.create();
  assert.equal(andOp.componentName, 'query-container/logical-operator');
  assert.equal(andOp.id, undefined);
  assert.equal(andOp.isFocused, false);
  assert.equal(andOp.isSelected, false);
  assert.equal(andOp.type, OPERATOR_AND);
});

test('can create a logical OR operator', function(assert) {
  const orOp = OperatorOr.create();
  assert.equal(orOp.componentName, 'query-container/logical-operator');
  assert.equal(orOp.id, undefined);
  assert.equal(orOp.isFocused, false);
  assert.equal(orOp.isSelected, false);
  assert.equal(orOp.type, OPERATOR_OR);
});