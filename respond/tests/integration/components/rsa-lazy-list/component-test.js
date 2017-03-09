import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('rsa-lazy-list', 'Integration | Component | Lazy List', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const items = [
  { id: 'a' },
  { id: 'b' },
  { id: 'c' }
];

test('it renders the correct number of list items in DOM', function(assert) {

  this.set('items', items);
  this.render(hbs`{{rsa-lazy-list items=items}}`);

  assert.equal(this.$('.rsa-lazy-list').length, 1, 'Expected to find the root component DOM element.');
  assert.equal(this.$('.rsa-list').length, 1, 'Expected to find the root element of child rsa-list component.');

  const $items = this.$('.rsa-list-item');
  assert.equal($items.length, items.length, 'Expected to find the list item DOM elements.');
});
