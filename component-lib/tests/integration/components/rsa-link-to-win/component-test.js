import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-link-to-win', 'Integration | Component | rsa-link-to-win', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{#rsa-link-to-win '/' target="_blank"}}Hello world{{/rsa-link-to-win}}`);
  assert.equal(this.$().length, 1, 'Expected root DOM node');
  assert.equal(this.$().text().trim(), 'Hello world', 'Expected to yield block');
});
