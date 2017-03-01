import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-panel/add-to-list/create-list-view', 'Integration | Component | context panel/add to list/create list view', {
  integration: true
});

test('it renders', function(assert) {

  this.set('createList', false);

  this.render(hbs`
    {{#context-panel/add-to-list/create-list-view}}
      createList=createList model=model
    {{/context-panel/add-to-list/create-list-view}}
  `);
  assert.equal(this.$().text().replace(/\s/g, ''), 'CreateNewListListNameDescriptionCancelSave');
});
