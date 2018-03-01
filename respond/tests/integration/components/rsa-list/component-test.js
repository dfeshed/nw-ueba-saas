import { run } from '@ember/runloop';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('rsa-list', 'Integration | Component | List', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const items = [
  { id: 'a', foo: 'foo-a' },
  { id: 'b', foo: 'foo-b' },
  { id: 'c', foo: 'foo-c' }
];

const selections = [ 'b', 'c' ];
const fooSelections = [ 'foo-a' ];

test('it renders the correct number of list items in DOM', function(assert) {

  this.set('items', items);
  this.render(hbs`{{#rsa-list items=items as |item|}}{{item.id}}{{/rsa-list}}`);

  assert.equal(this.$('.rsa-list').length, 1, 'Unable to find the root component DOM element.');

  const $items = this.$('.rsa-list-item');
  assert.equal($items.length, items.length, 'Unable to find the list item DOM elements.');
});

test('it marks selected items with the appropriate CSS class', function(assert) {

  this.setProperties({ items, selections });
  this.render(hbs`{{rsa-list items=items selections=selections}}`);

  assert.equal(this.$('.rsa-list').length, 1, 'Unable to find the root component DOM element.');

  const $items = this.$('.rsa-list-item');
  assert.notOk($items.first().hasClass('is-selected'), 'Expected to  NOT find is-selected CSS class applied in DOM.');
  assert.ok($items.eq(1).hasClass('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');
  assert.ok($items.last().hasClass('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');
});

test('it toggles the is-selected CSS class when a value is removed from selections', function(assert) {
  this.setProperties({ items, selections });
  this.render(hbs`{{rsa-list items=items selections=selections}}`);

  const $items = this.$('.rsa-list-item');
  assert.ok($items.last().hasClass('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');

  run(() => {
    selections.popObject();
  });
  assert.notOk($items.last().hasClass('is-selected'), 'Expected is-selected CSS class to be removed when selection is removed from array.');
});

test('it uses the itemIdField attr to determine which attr is the identifier', function(assert) {
  this.setProperties({ items, fooSelections });
  this.render(hbs`{{rsa-list items=items itemIdField="foo" selections=fooSelections}}`);

  const $items = this.$('.rsa-list-item');
  assert.ok($items.first().hasClass('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');
  assert.notOk($items.eq(1).hasClass('is-selected'), 'Expected to NOT find is-selected CSS class applied in DOM.');
  assert.notOk($items.last().hasClass('is-selected'), 'Expected to  NOT find is-selected CSS class applied in DOM.');
});

test('it invokes the clickAction with the corresponding item data when clicked', function(assert) {
  const clickAction = function(arg) {
    assert.ok(true, 'Expected clickAction to be invoked.');
    assert.equal(arg, items[0], 'Expected clickAction to received clicked item\'s corresponding data object.');
  };
  this.setProperties({ items, clickAction });
  this.render(hbs`{{rsa-list items=items singleSelectAction=clickAction}}`);

  const $items = this.$('.rsa-list-item');
  $items.first().click();
});