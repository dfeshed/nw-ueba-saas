import { module, test } from 'qunit';
import {
  isArrowDown,
  isArrowLeft,
  isArrowRight,
  isArrowUp,
  isBackspace,
  isDelete,
  isEnter,
  isEscape,
  isShift,
  isShiftTab,
  isSpace,
  isTab,
  isHome,
  isEnd
} from 'investigate-events/util/keys';

module('Unit | Util | keys');

test('properly identify Arrow Down key', function(assert) {
  assert.ok(isArrowDown({ key: 'ArrowDown' }));
  assert.notOk(isArrowDown({ key: 'x' }));
});

test('properly identify Arrow Left key', function(assert) {
  assert.ok(isArrowLeft({ key: 'ArrowLeft' }));
  assert.notOk(isArrowLeft({ key: 'x' }));
});

test('properly identify Arrow Right key', function(assert) {
  assert.ok(isArrowRight({ key: 'ArrowRight' }));
  assert.notOk(isArrowRight({ key: 'x' }));
});

test('properly identify Arrow Up key', function(assert) {
  assert.ok(isArrowUp({ key: 'ArrowUp' }));
  assert.notOk(isArrowUp({ key: 'x' }));
});

test('properly identify Backspace key', function(assert) {
  assert.ok(isBackspace({ key: 'Backspace' }));
  assert.notOk(isBackspace({ key: 'x' }));
});

test('properly identify Delete key', function(assert) {
  assert.ok(isDelete({ key: 'Delete' }));
  assert.notOk(isDelete({ key: 'x' }));
});

test('properly identify Enter key', function(assert) {
  assert.ok(isEnter({ key: 'Enter' }));
  assert.notOk(isEnter({ key: 'x' }));
});

test('properly identify Escape key', function(assert) {
  assert.ok(isEscape({ key: 'Escape' }));
  assert.notOk(isEscape({ key: 'x' }));
});

test('properly identify Space key', function(assert) {
  assert.ok(isSpace({ key: ' ' }));
  assert.notOk(isSpace({ key: 'x' }));
});

test('properly identify Shift key', function(assert) {
  assert.ok(isShift({ key: 'Shift' }));
  assert.notOk(isShift({ key: 'x' }));
});

test('properly identify Shift+Tab key combo', function(assert) {
  assert.ok(isShiftTab({ key: 'Tab', shiftKey: true }));
  assert.notOk(isShiftTab({ key: 'Tab', shiftKey: false }));
  assert.notOk(isShiftTab({ key: 'x', shiftKey: true }));
});

test('properly identify Tab key', function(assert) {
  assert.ok(isTab({ key: 'Tab' }));
  assert.notOk(isTab({ key: 'x' }));
});

test('properly identify Home key', function(assert) {
  assert.ok(isHome({ key: 'Home' }));
  assert.notOk(isArrowUp({ key: 'x' }));
});

test('properly identify End key', function(assert) {
  assert.ok(isEnd({ key: 'End' }));
  assert.notOk(isArrowUp({ key: 'x' }));
});