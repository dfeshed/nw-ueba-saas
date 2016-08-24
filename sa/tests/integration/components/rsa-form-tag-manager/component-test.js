import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-form-tag-manager', 'Integration | Component | rsa form tag manager', {
  integration: true,

  beforeEach() {
    this.set('mockCategoriesPicked',[]);
    this.set('mockTagData', [
      {
        'name': 'parentCategory1',
        'children': [
          {
            'id': 1,
            'name': 'childCategory1'
          },
          {
            'id': 2,
            'name': 'childCategory2'
          },
          {
            'id': 3,
            'name': 'childCategory3'
          },
          {
            'id': 4,
            'name': 'childCategory4'
          },
          {
            'id': 5,
            'name': 'childCategory5'
          }
        ]
      }
    ]);
  }
});

test('The rsa-form-tag-manager component renders in default state with the expected html structure.', function(assert) {
  this.render(hbs`{{rsa-form-tag-manager selectedTags=mockCategoriesPicked availableTags=mockTagData}}`);

  assert.equal(this.$('.rsa-form-tag-manager').length, 1, 'Testing to see if the .rsa-form-tag-manager element exists.');
  assert.equal(this.$('.rsa-form-tag-manager__tree-toggle .rsa-icon').length, 1, 'Testing to see if the .rsa-icon element exists.');
});

test('The rsa-form-tag-manager component renders in category selection state with the expected html structure.', function(assert) {
  this.set('contentTreeIsHidden', false);
  this.render(hbs`{{rsa-form-tag-manager selectedTags=mockCategoriesPicked contentTreeIsHidden=false availableTags=mockTagData}}`);

  assert.equal(this.$('.rsa-form-tag-manager').length, 1, 'Testing to see if the .rsa-form-tag-manager element exists.');
  assert.equal(this.$('.rsa-form-tag-manager .rsa-content-tree').length, 1, 'Testing to see if the .rsa-content-tree element exists.');
  assert.equal(this.$('.rsa-form-tag-manager .rsa-content-tree__tree-container .rsa-content-accordion h3').text().trim(), 'parentCategory1', 'Testing to see if the parent category label is displayed.');
  assert.equal(this.$('.rsa-form-tag-manager .rsa-content-tree__tree-container .rsa-content-tree__child-label').length, 5, 'Testing to see if the .rsa-content-tree__tree-container .rsa-content-tree__child-label elements exist.');
});
