import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import listData from '../../../../data/list';

moduleForComponent('context-panel/add-to-list', 'Integration | Component | context panel/add to list', {
  integration: true
});

test('Test to Display Add To List window.', function(assert) {
  this.set('listModel', {});

  this.set('listModel', listData);
  this.render(hbs`{{context-panel/add-to-list entityId=entityId entityType=entityType  getFilteredList=listModel}}`);
  assert.ok(this.$().text().indexOf('Add to List') != -1);
  assert.equal(this.$('.rsa-form-checkbox.checked').length, 3, 'Number of lists selected');
  assert.equal(this.$('.rsa-form-checkbox ').length - this.$('.rsa-form-checkbox.checked').length, 1, 'Number of lists unselected');
});
