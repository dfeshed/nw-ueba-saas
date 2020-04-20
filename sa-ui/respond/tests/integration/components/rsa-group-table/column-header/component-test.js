import EmberObject from '@ember/object';
import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { settled, findAll, find, render } from '@ember/test-helpers';

module('Integration | Component | rsa group table column header', function(hooks) {
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  test('it renders its default content without a block', async function(assert) {

    const column = EmberObject.create({
      field: 'foo',
      width: 75
    });
    this.set('column', column);
    await render(hbs`{{rsa-group-table/column-header column=column}}`);

    const cell = findAll('.rsa-group-table-column-header');
    assert.equal(cell.length, 1, 'Expected to find root DOM node.');
    assert.equal(cell[0].textContent.trim(), column.get('field'), 'Expected to find cell field in DOM');
    column.set('title', 'Foo Title');
    await settled().then(() => {
      const cellOne = find('.rsa-group-table-column-header');
      assert.equal(cellOne.textContent.trim(), column.get('title'), 'Expected to find cell title in DOM');
    });

  });

  test('it renders custom content when given a block', async function(assert) {

    const column = EmberObject.create({
      field: 'foo',
      width: 75
    });
    const index = 2;
    this.setProperties({
      column,
      index
    });

    await render(hbs`{{#rsa-group-table/column-header column=column index=index as |header|}}
    {{header.index}}:{{header.column.field}}
  {{/rsa-group-table/column-header}}`);

    const cell = findAll('.rsa-group-table-column-header');
    assert.equal(cell.length, 1, 'Expected to find root DOM node.');
    assert.equal(cell[0].textContent.trim(), `${index}:${column.get('field')}`, 'Expected to find custom cell content in DOM');
    column.set('title', 'Foo Title');
    await settled().then(() => {
      const cellOne = find('.rsa-group-table-column-header');
      assert.equal(cellOne.textContent.trim(), `${index}:${column.get('field')}`, 'Expected custom content to overwrite title');
    });
  });
});