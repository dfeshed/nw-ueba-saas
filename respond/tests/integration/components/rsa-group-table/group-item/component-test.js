import EmberObject from '@ember/object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import HasSelections from 'respond/mixins/group-table/has-selections';
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

const MockTableClass = EmberObject.extend(ComputesRowViewport, ComputesColumnExtents, HasSelections);

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

test('clicking on it fires the appropriate callback on the table parent', function(assert) {
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

  this.render(hbs`{{rsa-group-table/group-item
    item=item
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
  }}`);

  return wait()
    .then(() => {
      const row = this.$('.rsa-group-table-group-item');
      row.trigger('click');
      assert.equal(whichAction, 'itemClickAction', 'Expected click handler to be invoked');

      // eslint-disable-next-line new-cap
      const shiftClick = $.Event('click');
      shiftClick.shiftKey = true;
      row.trigger(shiftClick);
      assert.equal(whichAction, 'itemShiftClickAction', 'Expected SHIFT click handler to be invoked');

      // eslint-disable-next-line new-cap
      const ctrlClick = $.Event('click');
      ctrlClick.ctrlKey = true;
      row.trigger(ctrlClick);
      assert.equal(whichAction, 'itemCtrlClickAction', 'Expected CTRL click handler to be invoked');
    });
});

test('it applies the correct CSS class name when selected', function(assert) {

  table.set('selections', { areGroups: false, ids: [] });
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
      assert.ok(row.length);
      assert.notOk(row.hasClass('is-selected'));

      table.get('selections.ids').pushObject(item.id);
      return wait();
    }).then(() => {

      const row = this.$('.rsa-group-table-group-item');
      assert.ok(row.hasClass('is-selected'));
    });
});
