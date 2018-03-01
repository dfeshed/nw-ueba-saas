import EmberObject from '@ember/object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-group-table-column-header', 'Integration | Component | rsa group table column header', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('it renders its default content without a block', function(assert) {

  const column = EmberObject.create({
    field: 'foo',
    width: 75
  });
  this.set('column', column);
  this.render(hbs`{{rsa-group-table/column-header column=column}}`);

  return wait()
    .then(() => {
      const cell = this.$('.rsa-group-table-column-header');
      assert.equal(cell.length, 1, 'Expected to find root DOM node.');
      assert.equal(cell.text().trim(), column.get('field'), 'Expected to find cell field in DOM');

      column.set('title', 'Foo Title');
      return wait();
    })
    .then(() => {
      const cell = this.$('.rsa-group-table-column-header');
      assert.equal(cell.text().trim(), column.get('title'), 'Expected to find cell title in DOM');
    });
});

test('it renders custom content when given a block', function(assert) {

  const column = EmberObject.create({
    field: 'foo',
    width: 75
  });
  const index = 2;
  this.setProperties({
    column,
    index
  });

  this.render(hbs`{{#rsa-group-table/column-header column=column index=index as |header|}}
    {{header.index}}:{{header.column.field}}
  {{/rsa-group-table/column-header}}`);

  return wait()
    .then(() => {
      const cell = this.$('.rsa-group-table-column-header');
      assert.equal(cell.length, 1, 'Expected to find root DOM node.');
      assert.equal(cell.text().trim(), `${index}:${column.get('field')}`, 'Expected to find custom cell content in DOM');

      column.set('title', 'Foo Title');
      return wait();
    })
    .then(() => {
      const cell = this.$('.rsa-group-table-column-header');
      assert.equal(cell.text().trim(), `${index}:${column.get('field')}`, 'Expected custom content to overwrite title');
    });
});
