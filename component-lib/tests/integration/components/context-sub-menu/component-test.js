import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-menu-item/sub-menu', 'Integration | Component | context sub menu', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{context-menu-item/sub-menu}}`);
  assert.equal(this.$('ul.context-menu--sub').length, 1);
});

test('calculates offset before rendering', function(assert) {

  const item = {
    subActions: [
      {
        label: 'Item 1',
        action() { }
      },
      {
        label: 'Item 2',
        action() { }
      }
    ]
  };
  this.set('item', item);
  this.set('parentId', '123');
  this.render(hbs`<div id="123" style="position: absolute; top: 100px">{{context-menu-item/sub-menu parentId=parentId item=item}}</div>`);
  assert.ok(this.$('ul').attr('style'), 'Expected offset to be applied');
});