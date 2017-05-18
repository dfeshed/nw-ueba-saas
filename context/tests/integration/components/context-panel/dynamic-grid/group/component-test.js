import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-panel/dynamic-grid/group', 'Integration | Component | context panel/dynamic grid/group', {
  integration: true
});

test('it renders', function(assert) {

  const data = ['Test1', 'Test2', 'Test3'];
  const index = 1;
  const title = 'context.Business.Unit';
  this.set('data', data);
  this.set('title', title);
  this.set('index', index);
  this.render(hbs`{{context-panel/dynamic-grid/group groupData=data title=title index=index}}`);
  assert.ok(this.$().text().trim().indexOf('3') > -1);
});
