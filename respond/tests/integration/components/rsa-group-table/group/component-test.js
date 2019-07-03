import EmberObject from '@ember/object';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import HasSelections from 'respond/mixins/group-table/has-selections';
import { render, find, settled } from '@ember/test-helpers';

module('Integration | Component | rsa group table group', function(hooks) {

  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  const group = {
    id: 'id1',
    value: 'foo',
    items: [ 'a', 'b', 'c' ]
  };

  const index = 1;

  const top = 100;

  const MockTableClass = EmberObject.extend(ComputesRowViewport, ComputesColumnExtents, HasSelections);

  const table = MockTableClass.create();

  const initialState = {
    group,
    index,
    top,
    table
  };

  test('it renders and applies the correct top to its DOM node', async function(assert) {

    this.setProperties(initialState);

    await render(hbs`{{rsa-group-table/group
    group=group
    index=index
    top=top
    table=table
  }}`);

    const cell = find('.rsa-group-table-group');
    assert.ok(cell, 'Expected to find root DOM node');
    assert.equal(parseInt(cell.style.top, 10), top, 'Expected initial top to be applied to DOM');
    this.set('top', top * 2);
    await settled().then(() => {
      const firstCell = find('.rsa-group-table-group');
      assert.equal(parseInt(firstCell.style.top, 10), top * 2, 'Expected top to be updated in DOM');
    });
  });

  test('it applies the correct CSS class name when selected', async function(assert) {

    table.set('selections', { areGroups: true, ids: [] });
    this.setProperties(initialState);

    await render(hbs`{{rsa-group-table/group
    group=group
    index=index
    top=top
    table=table
  }}`);

    const row = find('.rsa-group-table-group');
    assert.notOk(row.classList.contains('is-selected'));
    table.get('selections.ids').pushObject(group.id);
    await settled().then(() => {
      const firstRow = find('.rsa-group-table-group');
      assert.ok(firstRow.classList.contains('is-selected'));
    });
  });
});