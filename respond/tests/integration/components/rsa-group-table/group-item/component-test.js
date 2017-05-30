import EmberObject from 'ember-object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import $ from 'jquery';

moduleForComponent('rsa-group-table-group-item', 'Integration | Component | rsa group table group item', {
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

const MockTableClass = EmberObject.extend(ComputesRowViewport, ComputesColumnExtents);

const groupItemSize = { outerHeight: 100 };

const table = MockTableClass.create({
  columnsConfig,
  groupItemSize
});

test('it renders and applies the correct top to its DOM node', function(assert) {

  this.setProperties({ item, relativeIndex, relativeIndexOffset, table });

  this.render(hbs`{{rsa-group-table/group-item
    item=item
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
  }}`);

  return wait()
    .then(() => {
      const row = this.$('.rsa-group-table-group-item');
      assert.ok(row.length, 'Expected to find root DOM node');
      assert.equal(
        row.find('.rsa-group-table-group-item__cell').length,
        visibleColumns.length,
        'Expected to find DOM nodes for each visible column by default'
      );

      assert.ok(
        row.css('transform'),
        'Expected initial transformY to be applied to DOM'
      );

      this.set('relativeIndex', relativeIndex + 1);
      return wait();
    })
    .then(() => {
      const row = this.$('.rsa-group-table-group-item');
      assert.ok(
        row.css('transform'),
        'Expected transformY to be updated in DOM'
      );
    });
});


test('it yields the item, index, column & columnIndex when a block is given', function(assert) {

  this.setProperties({ item, relativeIndex, relativeIndexOffset, table });

  this.render(hbs`{{#rsa-group-table/group-item
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

  return wait()
    .then(() => {
      const row = this.$('.rsa-group-table-group-item');
      assert.ok(row.length, 'Expected to find root DOM node');

      const blocks = row.find('.my-block');
      assert.equal(
        blocks.length,
        visibleColumns.length,
        'Expected to find a custom block for each visible columns'
      );

      blocks.each((i, el) => {
        const block = $(el);
        assert.equal(block.find('.item-id').text(), item.id, 'Expected to find item id in DOM');
        assert.equal(block.find('.index').text(), index, 'Expected to find item index in DOM');
        assert.equal(block.find('.column-field').text(), visibleColumns[i].field, 'Expected to find column field in DOM');
        assert.equal(block.find('.column-index').text(), String(i), 'Expected to find column index in DOM');
      });
    });
});