import { module, test } from 'qunit';
import columnUtil from 'investigate-events/components/events-table-container/row-container/column-util';
import { select } from 'd3-selection';

module('Unit | Events Table Row | Column util');

test('test span have meta value and name', function(assert) {
  const $cell = document.createElement('div');
  $cell.setAttribute('id', 'testId');
  const $el = select($cell);
  const field = 'ip.src';
  const item = {
    'ip.src': '10.10.10.10'
  };
  columnUtil.buildCellContent($el, field, item);
  const [innerSpan] = $cell.children[0].children;
  assert.equal(innerSpan.getAttribute('class'), 'entity');
  assert.equal(innerSpan.getAttribute('data-entity-id'), '10.10.10.10');
  assert.equal(innerSpan.getAttribute('data-meta-key'), 'ip.src');
});