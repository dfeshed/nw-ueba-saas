import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-panel/dynamic-grid/group', 'Integration | Component | context panel/dynamic grid/group', {
  integration: true
});

test('it renders', function(assert) {

  const data = ['Test1', 'Test2', 'Test3'];
  this.set('data', data);
  this.render(hbs`{{context-panel/dynamic-grid/group data=data}}`);
  assert.ok(this.$().text().trim().indexOf('3') > -1);
});
