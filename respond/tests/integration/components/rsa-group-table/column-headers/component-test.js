import EmberObject from '@ember/object';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, findAll } from '@ember/test-helpers';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';

module('Integration | Component | rsa group table column headers', function(hooks) {
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  const columnsConfig = [{
    field: 'foo',
    width: 75
  }, {
    field: 'bar',
    width: 50
  }];

  const MockTableClass = EmberObject.extend(ComputesRowViewport, ComputesColumnExtents);

  test('it renders itself and its default child components without a block', async function(assert) {

    const table = MockTableClass.create({
      columnsConfig
    });
    this.set('table', table);
    await render(hbs`{{#rsa-group-table/column-headers table=table as |column|}}
    {{column.cell}}
    {{/rsa-group-table/column-headers}}`);

    assert.equal(findAll('.rsa-group-table-column-headers').length, 1, 'Expected to find header root DOM node.');
    const headerCells = findAll('.rsa-group-table-column-header');
    assert.equal(headerCells.length, columnsConfig.length, 'Expected to find header cell for each column');

  });

  test('it renders itself and its child components when given a block', async function(assert) {

    const table = MockTableClass.create({
      columnsConfig
    });
    this.set('table', table);
    await render(hbs`{{#rsa-group-table/column-headers table=table as |column|}}
    <span class="my-content">{{column.index}}</span>
    {{/rsa-group-table/column-headers}}`);

    assert.equal(findAll('.rsa-group-table-column-headers').length, 1, 'Expected to find header root DOM node.');
    const headerCells = findAll('.my-content');
    assert.equal(headerCells.length, columnsConfig.length, 'Expected to find custom block cell for each column');

  });
});
