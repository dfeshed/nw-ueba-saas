import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-card', 'Integration | Component | rsa-content-card', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-content-card}}foo{{/rsa-content-card}}`);
  let cardCount = this.$().find('.rsa-content-card').length;
  assert.equal(cardCount, 1);
});
