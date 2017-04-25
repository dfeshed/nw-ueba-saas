import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-panel/data-table/body-cell', 'Integration | Component | context panel/data table/body cell', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  const column = {
    field: 'id',
    title: 'context.lc.md5',
    width: '100'
  };
  const item = {
    'id': 'ID1'
  };
  this.render(hbs`{{context-panel/data-table/body-cell}}`);

  assert.equal(this.$().text().trim(), '');
  this.set('column', column);
  this.set('item', item);
  // Template block usage:
  this.render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

  assert.equal(this.$().text().trim(), 'ID1');
});
