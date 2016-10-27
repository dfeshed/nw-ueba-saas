import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/query-input', 'Integration | Component | rsa-investigate/query-input', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/query-input}}`);
  assert.equal(this.$().text().trim(), '');

  // Just test that it can render for now.
  // There will be lots to test once the auto-complete features are implememted.
});
