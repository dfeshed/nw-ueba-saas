import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-content-tree', 'Integration | Component | rsa content tree', {
  integration: true,

  beforeEach() {
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

    this.set('mockAddAction', function(parentNode, childNode) {
      return `${parentNode}|${childNode}`;
    });

    this.set('mockToggleAction', function() {
      return false;
    });
  }
});

test('The rsa-content-tree component renders with the expected html structure.', function(assert) {
  this.render(hbs`{{rsa-content-tree rawCategoryData=mockTagData addAction=(action mockAddAction) toggleTreeVisibilityAction=(action mockToggleAction)}}`);

  assert.equal(this.$('.rsa-content-tree').length, 1, 'Testing to see if the .rsa-content-tree element exists.');
  assert.equal(this.$('.rsa-content-tree__arrow-container .rsa-icon').length, 1, 'Testing to see if the .rsa-content-tree__arrow-container .rsa-icon element exists.');
  assert.equal(this.$('.rsa-content-tree__tree-container').length, 1, 'Testing to see if the .rsa-content-tree__tree-container element exists.');
  assert.equal(this.$('.rsa-content-tree__tree-container .rsa-content-accordion h3').text().trim(), 'parentCategory1', 'Testing to see if the parent category label is displayed.');
  assert.equal(this.$('.rsa-content-tree__tree-container .rsa-content-tree__child-label').length, 5, 'Testing to see if the .rsa-content-tree__tree-container .rsa-content-tree__child-label elements exist.');
});
