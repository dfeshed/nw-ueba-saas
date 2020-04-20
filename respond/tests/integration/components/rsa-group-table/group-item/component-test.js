import EmberObject from '@ember/object';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import HasSelections from 'respond/mixins/group-table/has-selections';
import { render, find, settled, click } from '@ember/test-helpers';

module('Integration | Component | rsa group table group item', function(hooks) {

  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  const columnsConfig = [{
    field: 'id',
    width: 100,
    visible: true
  }, {
    field: 'foo',
    width: 100,
    visible: false
  }, {
    field: 'bar',
    width: 150,
    visible: true
  }];

  const visibleColumns = columnsConfig.filterBy('visible', true);

  const item = {
    id: 'id1',
    foo: 'foo-value',
    bar: 'bar-value'
  };

  const relativeIndex = 1;

  const relativeIndexOffset = 2;

  const index = relativeIndex + relativeIndexOffset;

  const MockTableClass = EmberObject.extend(ComputesRowViewport, ComputesColumnExtents, HasSelections);

  const groupItemSize = { outerHeight: 100 };

  const table = MockTableClass.create({
    columnsConfig,
    groupItemSize
  });

  test('it renders and applies the correct top to its DOM node', async function(assert) {

    this.setProperties({ item, relativeIndex, relativeIndexOffset, table });

    await render(hbs`{{rsa-group-table/group-item
    item=item
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
  }}`);

    const row = find('.rsa-group-table-group-item');
    assert.ok(row, 'Expected to find root DOM node');
    assert.equal(row.querySelectorAll('.rsa-group-table-group-item__cell').length, visibleColumns.length, 'Expected to find DOM nodes for each visible column by default');
    assert.ok(getComputedStyle(row).transform, 'Expected initial transformY to be applied to DOM');
    this.set('relativeIndex', relativeIndex + 1);
    await settled().then(() => {
      const firstRow = find('.rsa-group-table-group-item');
      assert.ok(getComputedStyle(firstRow).transform, 'Expected transformY to be updated in DOM');
    });

  });


  test('it yields the item, index, column & columnIndex when a block is given', async function(assert) {

    this.setProperties({ item, relativeIndex, relativeIndexOffset, table });

    await render(hbs`{{#rsa-group-table/group-item
    item=item
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
    as |rowCell|
  }}
    <div class="my-block">
      <span class="item-id">{{rowCell.item.id}}</span>
      <span class="index">{{rowCell.index}}</span>
      <span class="column-field">{{rowCell.column.field}}</span>
      <span class="column-index">{{rowCell.columnIndex}}</span>
  </div>
  {{/rsa-group-table/group-item}}`);


    const row = find('.rsa-group-table-group-item');
    assert.ok(row, 'Expected to find root DOM node');
    const blocks = row.querySelectorAll('.my-block');
    assert.equal(blocks.length, visibleColumns.length, 'Expected to find a custom block for each visible columns');
    blocks.forEach((block, i) => {
      assert.equal(block.querySelector('.item-id').textContent, item.id, 'Expected to find item id in DOM');
      assert.equal(block.querySelector('.index').textContent, index, 'Expected to find item index in DOM');
      assert.equal(block.querySelector('.column-field').textContent, visibleColumns[i].field, 'Expected to find column field in DOM');
      assert.equal(block.querySelector('.column-index').textContent, String(i), 'Expected to find column index in DOM');
    });
  });

  test('clicking on it fires the appropriate callback on the table parent', async function(assert) {
    let whichAction;
    table.setProperties({
      itemClickAction(payload) {
        assert.equal(payload.item, item, 'Expected callback to receive the item data object as an input param');
        whichAction = 'itemClickAction';
      },
      itemCtrlClickAction(payload) {
        assert.equal(payload.item, item, 'Expected callback to receive the item data object as an input param');
        whichAction = 'itemCtrlClickAction';
      },
      itemShiftClickAction(payload) {
        assert.equal(payload.item, item, 'Expected callback to receive the item data object as an input param');
        whichAction = 'itemShiftClickAction';
      }
    });
    this.setProperties({ item, relativeIndex, relativeIndexOffset, table });

    await render(hbs`{{rsa-group-table/group-item
    item=item
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
  }}`);

    await click('.rsa-group-table-group-item');
    assert.equal(whichAction, 'itemClickAction', 'Expected click handler to be invoked');
    // eslint-disable-next-line new-cap
    await click('.rsa-group-table-group-item', { shiftKey: true });
    assert.equal(whichAction, 'itemShiftClickAction', 'Expected SHIFT click handler to be invoked');
    // eslint-disable-next-line new-cap
    await click('.rsa-group-table-group-item', { ctrlKey: true });
    assert.equal(whichAction, 'itemCtrlClickAction', 'Expected CTRL click handler to be invoked');
  });

  test('it applies the correct CSS class name when selected', async function(assert) {

    table.set('selections', { areGroups: false, ids: [] });
    this.setProperties({ item, relativeIndex, relativeIndexOffset, table });

    await render(hbs`{{rsa-group-table/group-item
    item=item
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
  }}`);

    const row = find('.rsa-group-table-group-item');
    assert.ok(row);
    assert.notOk(row.classList.contains('is-selected'));
    table.get('selections.ids').pushObject(item.id);
    await settled().then(() => {
      const firstRow = find('.rsa-group-table-group-item');
      assert.ok(firstRow.classList.contains('is-selected'));
    });
  });
});