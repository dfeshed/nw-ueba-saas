import { module, test } from 'qunit';
import { calculateContextMenuOffset } from 'component-lib/utils/context-menu-utils';

module('Unit | Utils | Context Menu utils');

test('it calculates and returns Y-offset for context menu', function(assert) {
  const offset = calculateContextMenuOffset(5, 700, 600);
  assert.equal(offset, -78, 'Expected correct offset to be calculated');
});