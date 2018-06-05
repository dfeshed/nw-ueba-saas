import { module, test } from 'qunit';

import { calculateContextMenuOffset, componentCSSList, mergeObjectArray } from 'rsa-context-menu/utils/context-menu-utils';


module('Unit | Utils | Context Menu utils');

test('it calculates and returns Y-offset for context menu', function(assert) {
  const offset = calculateContextMenuOffset(5, 700, 600);
  assert.equal(offset, -78, 'Expected correct offset to be calculated');
});

test('it should return all supported CssClass List for context menu actions', function(assert) {
  const cssClassList = componentCSSList.EventAnalysisPanel;
  assert.equal(cssClassList.length, 3, 'Expected Three classes for EventAnalysisPanel');
  assert.equal(cssClassList.toString(), 'nw-event-value,nw-event-value-drillable-equals,nw-event-value-drillable-not-equals', 'Expected Three classes for EventAnalysisPanel');
});

test('mergeObjectArray should return combined object', function(assert) {
  const objA = [{ label: 'a' }, { label: 'b', subActions: [{ label: 'c' }] }];
  const objB = [{ label: 'd' }, { label: 'b', subActions: [{ label: 'e' }] }];
  const mergedObject = mergeObjectArray(objA, objB);
  assert.ok(mergedObject.find(({ label }) => label === 'a'));
  assert.ok(mergedObject.find(({ label }) => label === 'd'));
  assert.ok(mergedObject.find(({ label }) => label === 'b').subActions);
  assert.ok(mergedObject.find(({ label }) => label === 'b').subActions.find(({ label }) => label === 'c'));
  assert.ok(mergedObject.find(({ label }) => label === 'b').subActions.find(({ label }) => label === 'e'));
});