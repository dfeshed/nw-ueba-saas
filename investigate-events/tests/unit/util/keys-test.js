import { module, test } from 'qunit';
import { isArrowDown, isArrowLeft, isArrowRight, isArrowUp, isBackspace, isEnter, isEscape, isSpace } from 'investigate-events/util/keys';

module('Unit | Util | keys');

test('properly identify down arrow key', function(assert) {
  assert.ok(isArrowDown({ keyCode: 40 }));
  assert.notOk(isArrowDown({ keyCode: 99 }));
});

test('properly identify arrow left key', function(assert) {
  assert.ok(isArrowLeft({ keyCode: 37 }));
  assert.notOk(isArrowLeft({ keyCode: 99 }));
});

test('properly identify arrow right key', function(assert) {
  assert.ok(isArrowRight({ keyCode: 39 }));
  assert.notOk(isArrowRight({ keyCode: 99 }));
});

test('properly identify arrow up key', function(assert) {
  assert.ok(isArrowUp({ keyCode: 38 }));
  assert.notOk(isArrowUp({ keyCode: 99 }));
});

test('properly identify Backspace key', function(assert) {
  assert.ok(isBackspace({ keyCode: 8 }));
  assert.notOk(isBackspace({ keyCode: 99 }));
});

test('properly identify enter key', function(assert) {
  assert.ok(isEnter({ keyCode: 13 }));
  assert.notOk(isEnter({ keyCode: 99 }));
});

test('properly identify escape key', function(assert) {
  assert.ok(isEscape({ keyCode: 27 }));
  assert.notOk(isEscape({ keyCode: 99 }));
});

test('properly identify space key', function(assert) {
  assert.ok(isSpace({ keyCode: 32 }));
  assert.notOk(isSpace({ keyCode: 99 }));
});