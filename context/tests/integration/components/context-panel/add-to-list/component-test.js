import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-panel/add-to-list', 'Integration | Component | context panel/add to list', {
  integration: true
});

test('Test to Display Add To List window.', function(assert) {
  this.render(hbs`{{context-panel/add-to-list entityId=entityId entityType=entityType}}`);

  assert.ok(this.$('.modal-content').length);
});
