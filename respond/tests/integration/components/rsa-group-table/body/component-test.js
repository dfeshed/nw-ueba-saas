import EmberObject from '@ember/object';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, findAll } from '@ember/test-helpers';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import { isNumeric } from 'component-lib/utils/jquery-replacement';

module('Integration | Component | rsa group table body', function(hooks) {
  setupRenderingTest(hooks, {
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

  test('it renders itself and applies sizing to its DOM nodes', async function(assert) {

    const table = MockTableClass.create({ columnsConfig, groups });
    this.set('table', table);

    await render(hbs`{{rsa-group-table/body table=table}}`);
    assert.equal(findAll('.rsa-group-table-body').length, 1, 'Expected to find header root DOM node.');
    const placeholder = findAll('.js-placeholder');
    assert.equal(placeholder.length, 1, 'Expected to find placeholder DOM node');
    assert.ok(placeholder[0].style.height, 'Expected placeholder DOM to have some height applied to it');
    assert.ok(placeholder[0].style.width, 'Expected placeholder DOM to have some width applied to it');
    const sticky = findAll('.js-sticky-header');
    assert.equal(sticky.length, 1, 'Expected to find sticky DOM node');
    assert.ok(sticky[0].style.width, 'Expected sticky DOM to have some width applied to it');
    const stickyScroller = findAll('.js-sticky-header-scroller');
    assert.equal(stickyScroller.length, 1, 'Expected to find sticky DOM node');
    assert.ok(getComputedStyle(stickyScroller[0]).transform, 'Expected sticky DOM to have some transform applied to it');
  });

  test('it exposes attrs for size & scroll properties', async function(assert) {
    const table = MockTableClass.create({ columnsConfig, groups });
    this.set('table', table);

    await render(hbs`{{rsa-group-table/body table=table}}`);

    assert.ok(isNumeric(table.get('scrollerSize.innerWidth')));
    assert.ok(isNumeric(table.get('scrollerSize.innerHeight')));
    assert.ok(isNumeric(table.get('scrollerPos.top')));
    assert.ok(isNumeric(table.get('scrollerPos.left')));
  });
});