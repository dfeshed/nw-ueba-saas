import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-respond-index', 'Integration | Component | rsa respond/landing page/respond index', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index }}`);

  assert.equal(this.$('.rsa-respond-index-header').length, 1, 'Testing to see if a rsa-respond-index-header element exists.');
  assert.equal(this.$('.rsa-respond-card, .rsa-respond-list').length, 1, 'Testing to see if rsa-respond-card or rsa-respond-list exists.');
});
