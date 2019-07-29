import { module, test } from 'qunit';
import { CloseParen, OpenParen } from 'investigate-events/util/grammar-types';
import { CLOSE_PAREN, OPEN_PAREN } from 'investigate-events/constants/pill';

module('Unit | Util | Grammar Types');

test('can create a close paren', function(assert) {
  const closeParen = CloseParen.create();
  assert.equal(closeParen.componentName, 'query-container/close-paren');
  assert.equal(closeParen.id, undefined);
  assert.equal(closeParen.isFocused, false);
  assert.equal(closeParen.isSelected, false);
  assert.equal(closeParen.type, CLOSE_PAREN);
});

test('can create an open paren', function(assert) {
  const openParen = OpenParen.create();
  assert.equal(openParen.componentName, 'query-container/open-paren');
  assert.equal(openParen.id, undefined);
  assert.equal(openParen.isFocused, false);
  assert.equal(openParen.isSelected, false);
  assert.equal(openParen.type, OPEN_PAREN);
});