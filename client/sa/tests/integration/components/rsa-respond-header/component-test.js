import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import setupRouter from 'sa/tests/helpers/setup-router';

moduleForComponent('rsa-respond-header', 'Integration | Component | rsa respond header', {
  integration: true,
  setup() {
    setupRouter(this);
  }
});

test('The respond header component is rendered properly.', function(assert) {

  this.render(hbs`{{rsa-respond-header}}`);

  assert.equal(this.$('.rsa-respond-index-header__label').length, 1, 'Testing to see if a rsa-respond-index-header__label element exists.');
  assert.equal(this.$('.rsa-respond-index-header__tile-btn').length, 1, 'Testing to see if a rsa-respond-index-header__tile-btn exists.');
  assert.equal(this.$('.rsa-respond-index-header__list-btn').length, 1, 'Testing to see if a rsa-respond-index-header__list-btn element exists.');

});

test('The respond header saves the last selected view', function(assert) {

  this.render(hbs`{{rsa-respond-header}}`);

  this.$('.rsa-respond-index-header__tile-btn').trigger('click');
  assert.equal(this.$('.rsa-respond-index-header__tile-btn.is-active').length, 1, 'When clicking on Tile-button, it is set to selected.');
  assert.equal(this.$('.rsa-respond-index-header__list-btn.is-active').length, 0, 'When clicking on Tile-button, list button is un-selected.');

  this.$('.rsa-respond-index-header__list-btn').trigger('click');
  assert.equal(this.$('.rsa-respond-index-header__tile-btn.is-active').length, 0, 'When clicking on Tile-button, card button is un-selected.');
  assert.equal(this.$('.rsa-respond-index-header__list-btn.is-active').length, 1, 'When clicking on list-button, it is set to selected.');

});
