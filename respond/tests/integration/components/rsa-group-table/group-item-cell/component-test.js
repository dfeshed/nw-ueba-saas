import EmberObject from '@ember/object';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, findAll, find } from '@ember/test-helpers';

module('Integration | Component | rsa group table group item cell', function(hooks) {
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  const item = {
    id: 'id1',
    foo: 'bar'
  };

  const index = 2;

  const column = EmberObject.create({
    field: 'foo'
  });

  const columnIndex = 3;

  test('it renders default content without a block', async function(assert) {

    this.setProperties({
      item,
      index,
      column,
      columnIndex
    });

    await render(hbs`{{rsa-group-table/group-item-cell
    column=column
    item=item
    index=index
    columnIndex=columnIndex
  }}`);

    const cells = findAll('.rsa-group-table-group-item-cell');
    assert.ok(cells.length, 'Expected to find root DOM node');
    assert.equal(cells[0].textContent.trim(), item[column.get('field')], 'Expected content to be driven by column field by default');
  });

  test('it yields the item, item index, column & column index when a block is given', async function(assert) {
    this.setProperties({
      item,
      index,
      column,
      columnIndex
    });

    await render(hbs`{{#rsa-group-table/group-item-cell
    column=column
    item=item
    index=index
    columnIndex=columnIndex
    as |cell|
  }}
    <span class="item-id">{{cell.item.id}}</span>
    <span class="index">{{cell.index}}</span>
    <span class="column-field">{{cell.column.field}}</span>
    <span class="column-index">{{cell.columnIndex}}</span>
  {{/rsa-group-table/group-item-cell}}`);

    const cell = find('.rsa-group-table-group-item-cell');
    assert.equal(cell.querySelector('.item-id').textContent.trim(), item.id);
    assert.equal(cell.querySelector('.index').textContent.trim(), index);
    assert.equal(cell.querySelector('.column-index').textContent.trim(), columnIndex);
    assert.equal(cell.querySelector('.column-field').textContent.trim(), column.get('field'));
  });
});