import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import listData from '../../../../../data/list';

moduleForComponent('context-panel/add-to-list/list-view', 'Integration | Component | context panel/add to list/list view', {
  integration: true
});

test('Test to display list content in Add to List Window', function(assert) {

  this.set('listModel', {});
  this.set('listModel', listData);
  this.set('createList', true);
  this.set('model', {});

  this.render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
  getFilteredList=listModel}}`);
  assert.equal(this.$('.rsa-form-checkbox.checked').length, 3, 'Number of lists selected');
  assert.equal(this.$('.rsa-form-checkbox ').length - this.$('.rsa-form-checkbox.checked').length, 1,
  'Number of lists unselected');
});
