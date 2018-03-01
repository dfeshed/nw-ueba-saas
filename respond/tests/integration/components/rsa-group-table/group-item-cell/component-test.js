import EmberObject from '@ember/object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-group-table-group-item-cell', 'Integration | Component | rsa group table group item cell', {
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

test('it renders default content without a block', function(assert) {

  this.setProperties({
    item,
    index,
    column,
    columnIndex
  });

  this.render(hbs`{{rsa-group-table/group-item-cell
    column=column
    item=item
    index=index
    columnIndex=columnIndex
  }}`);

  return wait()
    .then(() => {
      const cell = this.$('.rsa-group-table-group-item-cell');
      assert.ok(cell.length, 'Expected to find root DOM node');
      assert.equal(cell.text().trim(), item[column.get('field')], 'Expected content to be driven by column field by default');
    });
});

test('it yields the item, item index, column & column index when a block is given', function(assert) {
  this.setProperties({
    item,
    index,
    column,
    columnIndex
  });

  this.render(hbs`{{#rsa-group-table/group-item-cell
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

  return wait()
    .then(() => {
      const cell = this.$('.rsa-group-table-group-item-cell');
      assert.equal(cell.find('.item-id').text().trim(), item.id);
      assert.equal(cell.find('.index').text().trim(), index);
      assert.equal(cell.find('.column-index').text().trim(), columnIndex);
      assert.equal(cell.find('.column-field').text().trim(), column.get('field'));
    });
});
