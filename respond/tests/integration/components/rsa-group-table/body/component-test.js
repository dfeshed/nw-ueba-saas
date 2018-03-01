import EmberObject from '@ember/object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import $ from 'jquery';

moduleForComponent('rsa-group-table-body', 'Integration | Component | rsa group table body', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const groups = [
  EmberObject.create({
    isOpen: true,
    items: (new Array(10)).fill({})
  }),
  EmberObject.create({
    isOpen: true,
    items: (new Array(5)).fill({})
  })
];

const columnsConfig = [{
  field: 'foo',
  width: 75
}, {
  field: 'bar',
  width: 50
}];

const MockTableClass = EmberObject.extend(ComputesRowViewport, ComputesColumnExtents);

test('it renders itself and applies sizing to its DOM nodes', function(assert) {

  const table = MockTableClass.create({ columnsConfig, groups });
  this.set('table', table);

  this.render(hbs`{{rsa-group-table/body table=table}}`);

  return wait()
    .then(() => {
      assert.equal(this.$('.rsa-group-table-body').length, 1, 'Expected to find header root DOM node.');

      const placeholder = this.$('.js-placeholder');
      assert.equal(placeholder.length, 1, 'Expected to find placeholder DOM node');
      assert.ok(placeholder[0].style.height, 'Expected placeholder DOM to have some height applied to it');
      assert.ok(placeholder[0].style.width, 'Expected placeholder DOM to have some width applied to it');

      const sticky = this.$('.js-sticky-header');
      assert.equal(sticky.length, 1, 'Expected to find sticky DOM node');
      assert.ok(sticky[0].style.width, 'Expected sticky DOM to have some width applied to it');

      const stickyScroller = this.$('.js-sticky-header-scroller');
      assert.equal(stickyScroller.length, 1, 'Expected to find sticky DOM node');
      assert.ok(stickyScroller.css('transform'), 'Expected sticky DOM to have some transform applied to it');
    });
});

test('it exposes attrs for size & scroll properties', function(assert) {
  const table = MockTableClass.create({ columnsConfig, groups });
  this.set('table', table);

  this.render(hbs`{{rsa-group-table/body table=table}}`);

  return wait()
    .then(() => {
      assert.ok($.isNumeric(table.get('scrollerSize.innerWidth')));
      assert.ok($.isNumeric(table.get('scrollerSize.innerHeight')));
      assert.ok($.isNumeric(table.get('scrollerPos.top')));
      assert.ok($.isNumeric(table.get('scrollerPos.left')));
    });
});
