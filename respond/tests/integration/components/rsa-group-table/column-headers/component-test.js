import EmberObject from '@ember/object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';

moduleForComponent('rsa-group-table-header', 'Integration | Component | rsa group table column headers', {
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

test('it renders itself and its default child components without a block', function(assert) {

  const table = MockTableClass.create({
    columnsConfig
  });
  this.set('table', table);
  this.render(hbs`{{#rsa-group-table/column-headers table=table as |column|}}
    {{column.cell}}
    {{/rsa-group-table/column-headers}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-group-table-column-headers').length, 1, 'Expected to find header root DOM node.');

    const headerCells = this.$('.rsa-group-table-column-header');
    assert.equal(headerCells.length, columnsConfig.length, 'Expected to find header cell for each column');
  });

});

test('it renders itself and its child components when given a block', function(assert) {

  const table = MockTableClass.create({
    columnsConfig
  });
  this.set('table', table);
  this.render(hbs`{{#rsa-group-table/column-headers table=table as |column|}}
    <span class="my-content">{{column.index}}</span>
    {{/rsa-group-table/column-headers}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-group-table-column-headers').length, 1, 'Expected to find header root DOM node.');

    const headerCells = this.$('.my-content');
    assert.equal(headerCells.length, columnsConfig.length, 'Expected to find custom block cell for each column');
  });

});

