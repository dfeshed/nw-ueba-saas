import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/meta/key-values', 'Integration | Component | rsa investigate/meta/key values', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/meta/key-values}}`);

  assert.equal(this.$('.rsa-investigate-meta-key-values').length, 1);
});
